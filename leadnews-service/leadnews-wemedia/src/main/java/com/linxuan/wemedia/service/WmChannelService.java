package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.ChannelDto;
import com.linxuan.model.wemedia.pojos.WmChannel;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询文章列表信息
     *
     * @return
     */
    public ResponseResult findAll();

    /**
     * 保存
     *
     * @param wmChannel
     * @return
     */
    ResponseResult insert(WmChannel wmChannel);

    /**
     * 查询
     *
     * @param dto
     * @return
     */
    ResponseResult findByNameAndPage(ChannelDto dto);

    /**
     * 修改
     *
     * @param wmChannel
     * @return
     */
    ResponseResult update(WmChannel wmChannel);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);
}
