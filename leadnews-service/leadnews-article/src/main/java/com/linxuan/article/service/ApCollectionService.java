package com.linxuan.article.service;

import com.linxuan.model.article.dtos.CollectionBehaviorDto;
import com.linxuan.model.common.dtos.ResponseResult;

public interface ApCollectionService {

    /**
     * 收藏操作
     * @param dto
     * @return
     */
    public ResponseResult collection(CollectionBehaviorDto dto);
}
