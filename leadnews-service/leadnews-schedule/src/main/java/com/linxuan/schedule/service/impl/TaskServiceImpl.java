package com.linxuan.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.linxuan.common.constans.ScheduleConstants;
import com.linxuan.common.redis.CacheService;
import com.linxuan.model.schedule.dtos.Task;
import com.linxuan.model.schedule.pojos.Taskinfo;
import com.linxuan.model.schedule.pojos.TaskinfoLogs;
import com.linxuan.schedule.mapper.TaskinfoLogsMapper;
import com.linxuan.schedule.mapper.TaskinfoMapper;
import com.linxuan.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

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
        // 获取Reids中存储的键
        String key = getCacheKey(task);

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
     * 获取到Redis中存储该Task的键
     *
     * @param task 根据taskType+priority确定键
     * @return 返回键
     */
    private String getCacheKey(Task task) {
        if (task == null || task.getTaskType() == null || task.getPriority() == null) {
            throw new RuntimeException("获取该Task在Redis中存储的键出问题");
        }
        return task.getTaskType() + "_" + task.getPriority();
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
        String key = getCacheKey(task);

        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            // 0是删除所有和 JSON.toJSONString(task) 匹配的数据
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }
}
