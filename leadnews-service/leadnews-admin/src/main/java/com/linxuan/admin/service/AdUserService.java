package com.linxuan.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.admin.dtos.AdUserDto;
import com.linxuan.model.admin.pojos.AdUser;
import com.linxuan.model.common.dtos.ResponseResult;

public interface AdUserService extends IService<AdUser> {

    /**
     * 登录
     * @param dto
     * @return
     */
    public ResponseResult login(AdUserDto dto);
}
