package com.linxuan.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linxuan.common.constans.ScheduleConstants;
import com.linxuan.common.redis.CacheService;
import com.linxuan.model.schedule.dtos.Task;
import com.linxuan.model.schedule.pojos.Taskinfo;
import com.linxuan.model.schedule.pojos.TaskinfoLogs;
import com.linxuan.schedule.mapper.TaskinfoLogsMapper;
import com.linxuan.schedule.mapper.TaskinfoMapper;
import com.linxuan.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    @Override
    public long addTask(Task task) {
        // 校验参数
        if (task == null)
            return 0;

        // 添加任务到DB中
        if (addTaskToDB(task)) {
            // 添加任务到Redis中
            addTaskToCache(task);
        }

        // 返回任务ID
        return task.getTaskId();
    }

    /**
     * 取消任务
     *
     * @param taskId 需要取消的任务的ID
     * @return true取消成功、false取消失败
     */
    @Override
    public boolean cancelTask(long taskId) {
        // 删除任务 更新日志状态为2=CANCELLED已取消；
        // 消费任务也是删除任务 更新日志状态 只不过将日志状态更改为1=EXECUTED已执行
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);

        if (task != null) {
            // 删除Redis中存储的任务
            removeTaskFromCache(task);
            return true;
        }
        return false;
    }

    /**
     * 按照类型和优先级来拉取任务消费
     *
     * @param type     任务类型
     * @param priority 任务优先级
     * @return 返回任务Task对象
     */
    @Override
    public Task pollTask(int type, int priority) {
        Task task = null;
        try {
            // 获取Redis中存储数据的key
            String key = getCacheKey(type, priority);
            // 从list里面获取要消费的任务
            String taskJson = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(taskJson)) {
                task = JSON.parseObject(taskJson, Task.class);
                // 修改DB该Task状态 taskinfo删除该Task taskinfo_logs修改状态为已消费
                updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }

        return task;
    }

    /**
     * Redis中存储的未来数据从zset定时刷新至list中, 每分钟刷新一次
     *
     * @Scheduled: 定时任务。引导类中添加开启任务调度注解：@EnableScheduling
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refreshTaskToList() {
        // 加锁
        String token = cacheService.tryLock(ScheduleConstants.FUTURE_TASK_SYNC, 1000 * 30);
        if (StringUtils.isNotBlank(token)) {
            // 获取zset中存储的所有未来5分钟内要执行的任务数据集合的key值
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                // 获取该组key对应的需要消费的任务数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if (!tasks.isEmpty()) {
                    // 设置当前任务数据放到list中后的key
                    String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];
                    // 将任务数据添加到消费者队列list中 使用Redis管道技术(效率更高)
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    System.out.println("将" + futureKey + "下当前需要执行任务数据刷新到" + topicKey);
                }
            }
        }

    }

    /**
     * 定时加载DB中的数据同步至Redis缓存中，每5分钟调用一次
     *
     * @PostConstruct: 项目启动直接调用该方法
     */
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * ?")
    public void reloadDataToCache() {
        // 清理Redis中数据，避免任务重复造成多次执行
        clearCache();

        // 从DB获取小于未来5分钟的所有任务
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        List<Taskinfo> taskinfos = taskinfoMapper.selectList(new LambdaQueryWrapper<Taskinfo>()
                .lt(Taskinfo::getExecuteTime, calendar.getTime()));

        // 同步Redis中
        if (taskinfos != null && !taskinfos.isEmpty()) {
            for (Taskinfo taskinfo : taskinfos) {
                // 构造Task
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                // 添加到Redis中
                addTaskToCache(task);
            }
        }
    }


    /**
     * 添加任务到DB
     *
     * @param task 任务
     * @return true添加成功 false添加失败
     */
    private boolean addTaskToDB(Task task) {

        boolean flag = false;
        try {
            // 创建Taskinfo存储对象
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            // Task类中执行时间executeTime类型为long、TaskInfo类中执行时间executeTime类型为Date
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            // 存储
            taskinfoMapper.insert(taskinfo);

            // Task是地址引用，设置它的taskId 最后返回
            task.setTaskId(taskinfo.getTaskId());

            // 创建TaskinfoLogs存储对象
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 任务添加到Redis中
     *
     * @param task 需要添加的任务
     */
    private void addTaskToCache(Task task) {
        // 根据任务类型和任务优先级组成Reids中存储的键
        String key = getCacheKey(task.getTaskType(), task.getPriority());

        // 获取当前时间 + 5分钟
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        // 如果任务执行的时间小于当前时间 存储list
        if (task.getExecuteTime() < System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            // 任务执行时间小于预设时间(当前时间 + 5分钟) 存储zset 键、值、分数
            cacheService.zAdd(ScheduleConstants.FUTURE + key,
                    JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    /**
     * 根据taskType和priority组成Redis中存储的key
     *
     * @param taskType 任务类型
     * @param priority 任务优先级
     * @return 返回key
     */
    private String getCacheKey(Integer taskType, Integer priority) {
        if (taskType == null || priority == null) {
            throw new RuntimeException("Redis中存储键异常");
        }
        return taskType + "_" + priority;
    }

    /**
     * 取消任务 更新日志中任务的状态
     *
     * @param taskId 需要取消的任务
     * @param status 更改为该状态
     * @return 返回Task用以找寻Redis中存储的该任务
     */
    private Task updateDb(long taskId, int status) {
        Task task = null;
        try {
            // DB中taskinfo表删除该任务
            int result = taskinfoMapper.deleteById(taskId);
            if (result == 0) {
                throw new RuntimeException("删除任务失败");
            }

            // taskinfo_logs更新任务状态
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            // 构建返回的Task对象
            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel exception taskid={}", taskId);
        }

        return task;
    }

    /**
     * 移除Redis中存储的任务Task
     *
     * @param task 通过taskType+priority确定键、executeTime确定从list还是zset中删除
     */
    private void removeTaskFromCache(Task task) {
        // 获取Redis中存储该Task的key
        String key = getCacheKey(task.getTaskType(), task.getPriority());

        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            // 0是删除所有和 JSON.toJSONString(task) 匹配的数据
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    /**
     * 清理Redis中的数据 删除缓存中未来数据集合和当前消费者队列的所有key
     */
    private void clearCache() {
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(futureKeys);
        cacheService.delete(topicKeys);
    }
}
