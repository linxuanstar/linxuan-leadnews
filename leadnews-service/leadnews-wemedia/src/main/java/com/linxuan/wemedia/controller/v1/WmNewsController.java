package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.WmNewsDto;
import com.linxuan.model.wemedia.dtos.WmNewsPageReqDto;
import com.linxuan.wemedia.service.WmNewsAutoScanService;
import com.linxuan.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;

@Slf4j
@RestController
@RequestMapping("/api/v1/news/")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.findAll(dto);
    }

    /**
     * 文章发布及修改
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {

        return wmNewsService.submitNews(dto);
    }
}
