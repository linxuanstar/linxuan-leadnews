package com.linxuan.user.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.user.dtos.LoginDto;
import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.user.service.impl.ApUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserServiceImpl apUserService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto){

        return apUserService.login(loginDto);
    }
}
