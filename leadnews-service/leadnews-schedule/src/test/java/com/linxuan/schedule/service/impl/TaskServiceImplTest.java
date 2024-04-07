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
        Task task = new Task();
        task.setTaskType(100);
        task.setPriority(50);
        task.setParameters("task test".getBytes());
        task.setExecuteTime(new Date().getTime());

        System.out.println(taskService.addTask(task));
    }

    @Test
    public void cancelTaskTest() {
        System.out.println(taskService.cancelTask(1776840007500185601L));
    }
}
