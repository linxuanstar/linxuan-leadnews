package com.linxuan.freemarker.test;

import com.linxuan.freemarker.FreeMarkerApplication;
import com.linxuan.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FreeMarkerApplication.class)
public class FreemarkerTest {

    @Autowired
    private Configuration configuration;

    /**
     * 测试输出静态化文件
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void test01() throws IOException, TemplateException {
        // 获取模板对象，需要事先在application.yml配置ftl路径
        Template template = configuration.getTemplate("02-list.ftl");
        // 获取数据对象
        Map<String, Object> data = getData();
        // 合成文件并输出
        template.process(data, new FileWriter("d:/list.html"));
    }

    private Map<String, Object> getData() {
        // 这里这个map就代替之前的model了
        Map<String, Object> map = new HashMap<>();

        Student stu1 = Student.builder()
                .name("小强").age(18).money(1000.86f)
                .birthday(new Date())
                .build();

        Student stu2 = Student.builder()
                .name("小红").age(16).money(1660.86f)
                .build();
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        map.put("stus", stus);


        Map<String, Object> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        map.put("stuMap", stuMap);

        return map;
    }
}
