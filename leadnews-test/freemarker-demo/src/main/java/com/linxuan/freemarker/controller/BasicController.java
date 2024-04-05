package com.linxuan.freemarker.controller;

import com.linxuan.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.jws.WebParam;
import java.util.*;

@Controller
public class BasicController {

    @GetMapping("/basic")
    public String basic(Model model) {

        model.addAttribute("name", "linxuan");
        // Student stu = Student.builder().name("林炫").age(18).build();
        // model.addAttribute("stu", stu);

        return "01-basic";
    }

    @GetMapping("/list")
    public String list(Model model) {

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

        model.addAttribute("stus", stus);

        return "01-basic";
    }

    @GetMapping("/map")
    public String map(Model model) {
        Student stu1 = Student.builder()
                .name("小强").age(18).money(1000.86f)
                .birthday(new Date())
                .build();

        Student stu2 = Student.builder()
                .name("小红").age(16).money(1660.86f)
                .build();

        Map<String, Object> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        model.addAttribute("stuMap", stuMap);
        return "01-basic";
    }

}
