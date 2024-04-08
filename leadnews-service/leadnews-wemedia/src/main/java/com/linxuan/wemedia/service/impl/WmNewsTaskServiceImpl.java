package com.linxuan.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.linxuan.feign.api.schedule.IScheduleClient;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.TaskTypeEnum;
import com.linxuan.model.schedule.dtos.Task;
import com.linxuan.model.wemedia.pojos.WmNews;
import com.linxuan.utils.common.ProtostuffUtil;
import com.linxuan.wemedia.service.WmNewsAutoScanService;
import com.linxuan.wemedia.service.WmNewsTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient iScheduleClient;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;


    /**
     * 添加审核自媒体文章任务到延迟队列中
     *
     * @param id          需要审核的自媒体文章ID
     * @param publishTime 　文章发布时间　同时也是审核时间
     */
    @Async
    @Override
    public void addNewsToTask(Integer id, Date publishTime) {

        // 设置添加的任务对象
        Task task = new Task();
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (publishTime == null) {
            task.setExecuteTime(new Date().getTime());
        } else {
            task.setExecuteTime(publishTime.getTime());
        }
        // 最后文章审核端从延迟任务队列拉取任务消费 只需要获取文章ID调用autoScanWmNews审核
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        // 调用feign远程接口 添加延迟任务
        iScheduleClient.addTask(task);
    }

    /**
     * 定时消费延迟队列数据 每秒钟拉取一次任务
     */
    @Override
    @SneakyThrows
    @Scheduled(fixedRate = 1000)
    public void scanNewsByTask() {
        log.info("文章审核---消费任务执行---begin---");

        // 调用schedule端消费数据方法
        ResponseResult responseResult = iScheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(),
                TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        // 返回的数据没有问题
        if (responseResult.getCode().equals(200) && responseResult.getData() != null) {
            // 对数据解析为Task对象
            String taskJsonStr = JSON.toJSONString(responseResult.getData());
            Task task = JSON.parseObject(taskJsonStr, Task.class);
            // 解析Task对象参数parameters为wmNews
            byte[] parameters = task.getParameters();
            WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);

            // 调用自动审核方法
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        }

        log.info("文章审核---消费任务执行---end---");
    }
}
