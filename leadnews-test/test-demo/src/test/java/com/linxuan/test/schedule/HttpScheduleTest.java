package com.linxuan.test.schedule;

import com.linxuan.test.TestApplication;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class HttpScheduleTest {

    @Test
    public void testHttp() {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("www.baidu.com") // 目标 URL
                .build();

        // 发送请求，并获取响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 判断请求是否成功
            if (response.isSuccessful()) {
                // 打印响应的 body 内容
                System.out.println(response.body().string());
            } else {
                System.out.println("请求失败，代码：" + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
