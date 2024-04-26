package com.linxuan.admin.controller.v1;


import com.linxuan.admin.service.AdUserService;
import com.linxuan.model.admin.dtos.AdUserDto;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AdUserService adUserService;

    /**
     * 管理端登录
     *
     * @param dto
     * @return
     */
    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDto dto) {
        return adUserService.login(dto);
    }
}
