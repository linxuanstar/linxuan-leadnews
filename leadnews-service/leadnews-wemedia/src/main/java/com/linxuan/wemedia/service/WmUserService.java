package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.WmLoginDto;
import com.linxuan.model.wemedia.pojos.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     *
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

}