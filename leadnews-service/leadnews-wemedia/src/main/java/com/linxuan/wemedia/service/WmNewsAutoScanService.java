package com.linxuan.wemedia.service;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.pojos.WmNews;

/**
 * 文章自动审核
 */
public interface WmNewsAutoScanService {

    /**
     * 自媒体文章自动审核
     *
     * @param id 自媒体端文章ID
     */
    void autoScanWmNews(Integer id);

    /**
     * 保存app文章数据
     *
     * @param wmNews
     * @return
     */
    public ResponseResult saveAppArticle(WmNews wmNews);
}
