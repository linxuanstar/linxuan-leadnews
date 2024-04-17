package com.linxuan.search.service;

public interface ApUserSearchService {
    /**
     * 保存用户搜索历史记录
     *
     * @param keyword 用户搜索的关键字
     * @param userId  用户ID
     */
    void insert(String keyword, Integer userId);
}
