package com.linxuan.model.wemedia.dtos;

import com.linxuan.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * 文章审核相关功能前端传过来的参数
 */
@Data
public class NewsAuthDto extends PageRequestDto {

    /**
     * 文章标题
     */
    private String title;
    /**
     * 状态
     */
    private Short status;
    /**
     * 自媒体文章id
     */
    private Integer id;
    /**
     * 审核失败的原因
     */
    private String msg;
}