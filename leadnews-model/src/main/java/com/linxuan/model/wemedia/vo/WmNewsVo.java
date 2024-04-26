package com.linxuan.model.wemedia.vo;

import com.linxuan.model.wemedia.pojos.WmNews;
import lombok.Data;

/**
 * 传给前端的数据
 */
@Data
public class WmNewsVo extends WmNews {
    /**
     * 作者名称
     */
    private String authorName;
}