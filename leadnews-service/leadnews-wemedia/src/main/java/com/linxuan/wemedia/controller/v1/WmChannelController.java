package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.ChannelDto;
import com.linxuan.model.wemedia.pojos.WmChannel;
import com.linxuan.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel/")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    /**
     * 查询文章频道列表信息
     *
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }

    /**
     * 管理端——查询文章频道列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findByNameAndPage(@RequestBody ChannelDto dto) {
        return wmChannelService.findByNameAndPage(dto);
    }

    /**
     * 管理端——新增文章频道
     *
     * @param adChannel
     * @return
     */
    @PostMapping("/save")
    public ResponseResult insert(@RequestBody WmChannel adChannel) {
        return wmChannelService.insert(adChannel);
    }

    /**
     * 管理端——更新文章频道
     *
     * @param adChannel
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmChannel adChannel) {
        return wmChannelService.update(adChannel);
    }

    /**
     * 管理端——删除文章频道
     *
     * @param id
     * @return
     */
    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        return wmChannelService.delete(id);
    }

}
