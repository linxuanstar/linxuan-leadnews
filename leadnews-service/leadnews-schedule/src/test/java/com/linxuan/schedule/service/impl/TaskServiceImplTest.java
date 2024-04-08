package com.linxuan.schedule.service.impl;

import com.linxuan.model.schedule.dtos.Task;
import com.linxuan.schedule.ScheduleApplication;
import com.linxuan.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScheduleApplication.class)
public class TaskServiceImplTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void addTask() {
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100 + i);
            task.setPriority(50);
            task.setParameters("task test".getBytes());
            // 设置执行时间为当前时间+500 * i，保证任务放到zset中
            task.setExecuteTime(new Date().getTime() + 500 * i);
            taskService.addTask(task);
        }
    }

    @Test
    public void cancelTaskTest() {
        System.out.println(taskService.cancelTask(1776840007500185601L));
    }

    @Test
    public void pollTest() {
        System.out.println(taskService.pollTask(100, 50));
    }
}
