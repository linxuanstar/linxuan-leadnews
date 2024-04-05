package com.linxuan.tess4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linxuan.model.wemedia.pojos.WmSensitive;
import com.linxuan.utils.common.SensitiveWordUtil;
import com.linxuan.wemedia.mapper.WmSensitiveMapper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
