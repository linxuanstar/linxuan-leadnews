package com.linxuan.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.user.dtos.LoginDto;
import com.linxuan.model.user.pojos.ApUser;

public interface ApUserService extends IService<ApUser> {

    /**
     * app端用户登录功能
     * @param loginDto 前端参数
     * @return
     */
    public ResponseResult login(LoginDto loginDto);
}
