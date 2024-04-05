package com.linxuan.freemarker.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Student {
    private String name; // 姓名
    private int age; // 年龄
    private Date birthday; // 生日
    private Float money; // 钱包
}