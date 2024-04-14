package com.linxuan.search.service;

import com.linxuan.model.article.dtos.UserSearchDto;
import com.linxuan.model.common.dtos.ResponseResult;

import java.io.IOException;

public interface ArticleSearchService {
    /**
     * ES 根据条件分页查询文章列表
     *
     * @param userSearchDto 条件
     * @return 返回分页查询结果
     * @throws IOException ES查询可能抛出IO异常
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}
