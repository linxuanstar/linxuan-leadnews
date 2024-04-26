package com.linxuan.behavior.service;

import com.linxuan.model.behavior.dtos.ReadBehaviorDto;
import com.linxuan.model.common.dtos.ResponseResult;

public interface ApReadBehaviorService {

    /**
     * 保存阅读行为
     * @param dto
     * @return
     */
    public ResponseResult readBehavior(ReadBehaviorDto dto);
}
