package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel/")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    /**
     * 查询文章频道列表信息
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }
}
