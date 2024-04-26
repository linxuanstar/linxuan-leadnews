package com.linxuan.minio.test;


import com.linxuan.file.service.FileStorageService;
import com.linxuan.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinIOApplication.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void testMinIO() {
        Map<String, String> map = new HashMap<>();
        map.put("D:\\tmp\\plugins\\css\\index.css", "plugins/css/index.css");
        map.put("D:\\tmp\\plugins\\js\\index.js", "plugins/js/index.js");
        map.put("D:\\tmp\\plugins\\js\\axios.min.js", "plugins/js/axios.min.js");

        map.forEach((key, value) -> {
            int lastIndexOf = value.lastIndexOf(".");
            String suffix = null;
            if (lastIndexOf != -1) {
                suffix = value.substring(lastIndexOf + 1);
            } else {
                suffix = "css";
            }


            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(key);

                // 创建minio客户端
                MinioClient minioClient = MinioClient.builder()
                        .credentials("minio", "minio123")
                        .endpoint("http://192.168.88.129:9000")
                        .build();
                // 上传文件
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .object(value) // 创建文件名称
                        .contentType("text/" + suffix) // 文件类型
                        .bucket("leadnews") // 桶名词，和minio中创建的一致
                        .stream(fileInputStream, fileInputStream.available(), -1)
                        .build();
                minioClient.putObject(putObjectArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("D:\\image\\test01.jpg");
        String filePath = fileStorageService.uploadImgFile("", "test01.jpg", fileInputStream);
        System.out.println(filePath);
    }
}
