package com.linxuan.model.schedule.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class Task implements Serializable {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务类型, Schedule模块可以被不同的项目调用，可以处理不同类型的延迟任务
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行时间
     */
    private long executeTime;

    /**
     * task参数
     */
    private byte[] parameters;
}