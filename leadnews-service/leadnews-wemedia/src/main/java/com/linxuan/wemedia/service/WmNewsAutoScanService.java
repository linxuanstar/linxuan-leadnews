package com.linxuan.wemedia.service;

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
}
