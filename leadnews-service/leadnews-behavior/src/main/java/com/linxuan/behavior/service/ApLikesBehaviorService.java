package com.linxuan.behavior.service;

import com.linxuan.model.behavior.dtos.LikesBehaviorDto;
import com.linxuan.model.common.dtos.ResponseResult;

public interface ApLikesBehaviorService {

    /**
     * 存储喜欢数据
     * @param dto
     * @return
     */
    public ResponseResult like(LikesBehaviorDto dto);
}
