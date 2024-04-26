package com.linxuan.behavior.service;

import com.linxuan.model.behavior.dtos.UnLikesBehaviorDto;
import com.linxuan.model.common.dtos.ResponseResult;

/**
 * <p>
 * APP不喜欢行为表 服务类
 * </p>
 *
 * @author linxuan
 */
public interface ApUnlikesBehaviorService {

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    public ResponseResult unLike(UnLikesBehaviorDto dto);

}