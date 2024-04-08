package com.linxuan.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {

    /**
     * 添加审核自媒体文章任务到延迟队列中
     *
     * @param id          需要审核的自媒体文章ID
     * @param publishTime 　文章发布时间　同时也是审核时间
     */
    void addNewsToTask(Integer id, Date publishTime);

    /**
     * 定时消费延迟队列数据 每秒钟拉取一次任务
     */
    void scanNewsByTask();
}
