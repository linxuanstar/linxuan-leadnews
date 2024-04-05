package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linxuan.model.wemedia.pojos.WmSensitive;
import com.linxuan.utils.common.SensitiveWordUtil;
import com.linxuan.wemedia.WemediaApplication;
import com.linxuan.wemedia.mapper.WmSensitiveMapper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WemediaApplication.class)
public class WmNewsAutoScanServiceTest {

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Test
    public void autoScanWmNews() {
        wmNewsAutoScanService.autoScanWmNews(6238);
    }
}
