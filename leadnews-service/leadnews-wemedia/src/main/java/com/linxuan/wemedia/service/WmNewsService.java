package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.WmNewsDto;
import com.linxuan.model.wemedia.dtos.WmNewsPageReqDto;
import com.linxuan.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult findAll(@RequestBody WmNewsPageReqDto dto);

    /**
     * 发布文章或者保存草稿
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(@RequestBody WmNewsDto dto);
}
