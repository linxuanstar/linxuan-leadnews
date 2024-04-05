package com.linxuan.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.user.dtos.LoginDto;
import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.user.mapper.ApUserMapper;
import com.linxuan.user.service.ApUserService;
import com.linxuan.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser>
        implements ApUserService {

    /**
     * app端用户登录功能
     *
     * @param loginDto 前端参数
     * @return
     */
    @Override
    public ResponseResult login(LoginDto loginDto) {
        if (StringUtils.isNotBlank(loginDto.getPhone()) && StringUtils.isNotBlank(loginDto.getPassword())) {
            // 判断用户是否存在
            ApUser dbUser = getOne(new LambdaQueryWrapper<ApUser>().eq(ApUser::getPhone, loginDto.getPhone()));
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST, "用户不存在");
            }

            // 用户存在校验密码
            String password = loginDto.getPassword();
            String salt = dbUser.getSalt();
            String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pwd.equals(dbUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码错误");
            }

            // 密码正确，用户通过校验，返回JWT
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            dbUser.setPassword("");
            dbUser.setSalt("");
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        }

        // 游客登录，JWT设置为0，直接返回即可
        Map<String, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(0L));
        return ResponseResult.okResult(map);
    }
}
