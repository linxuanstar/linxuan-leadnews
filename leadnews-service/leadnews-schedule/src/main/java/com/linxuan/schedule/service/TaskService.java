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
     *
     * @param taskId 需要取消的任务的ID
     * @return true取消成功、false取消失败
     */
    boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级来拉取任务消费
     *
     * @param type     任务类型
     * @param priority 任务优先级
     * @return 返回任务Task对象
     */
    Task pollTask(int type, int priority);
}