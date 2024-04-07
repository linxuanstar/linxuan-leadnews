package com.linxuan.schedule.service;

import com.linxuan.model.schedule.dtos.Task;

public interface TaskService {

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    long addTask(Task task);

    /**
     * 取消任务
     * @param taskId 需要取消的任务的ID
     * @return true取消成功、false取消失败
     */
    boolean cancelTask(long taskId);
}