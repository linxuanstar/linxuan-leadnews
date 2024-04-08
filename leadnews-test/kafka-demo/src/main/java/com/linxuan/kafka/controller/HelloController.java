package com.linxuan.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.linxuan.kafka.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/hello")
    public String hello() {
        // 第一个参数是topic 第二个参数是数据
        kafkaTemplate.send("linxuan-topic", "林炫你好");
        return "ok";
    }

    @GetMapping("/transUser")
    public String transUser() {
        User user = new User("林炫", 23);
        kafkaTemplate.send("user-topic", JSON.toJSONString(user));
        return "ok!";
    }

}