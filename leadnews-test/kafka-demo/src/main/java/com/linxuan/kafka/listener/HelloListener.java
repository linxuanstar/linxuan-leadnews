package com.linxuan.kafka.listener;


import com.alibaba.fastjson.JSON;
import com.linxuan.kafka.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HelloListener {
    /**
     * 监听linxuan-topic该主题 一旦有消息那么就注入
     *
     * @param message
     */
    @KafkaListener(topics = "linxuan-topic")
    public void onMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            System.out.println(message);
        }
    }

    @KafkaListener(topics = "user-topic")
    public void onMessageForUser(String message) {
        if (!StringUtils.isEmpty(message)) {
            User user = JSON.parseObject(message, User.class);
            System.out.println(user);
        }
    }
}