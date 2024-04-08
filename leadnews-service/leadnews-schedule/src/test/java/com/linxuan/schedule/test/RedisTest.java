package com.linxuan.schedule.test;

import com.linxuan.common.redis.CacheService;
import com.linxuan.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScheduleApplication.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void testList() {
        // 在list的左边添加元素
        cacheService.lLeftPush("list_001", "hello,redis");

        // 在list的右边获取元素，并删除
        System.out.println(cacheService.lRightPop("list_001"));
    }

    @Test
    public void testZset() {
        // 添加数据到zset中  分值
        cacheService.zAdd("zset_key_001", "hello zset 001", 1000);
        cacheService.zAdd("zset_key_001", "hello zset 002", 8888);
        cacheService.zAdd("zset_key_001", "hello zset 003", 7777);
        cacheService.zAdd("zset_key_001", "hello zset 004", 999999);

        // 按照分值获取数据
        Set<String> zset_key_001 = cacheService.zRangeByScore("zset_key_001", 0, 8888);
        System.out.println(zset_key_001);
    }

    @Test
    public void testKeys() {
        // 使用keys方式模糊匹配所有的前缀为future_的所有key 生产环境不要使用
        Set<String> keys = cacheService.keys("future_*");
        System.out.println(keys);

        // 使用scan方式模糊匹配所有的前缀为future_的所有key
        Set<String> scan = cacheService.scan("future_*");
        System.out.println(scan);
    }
}
