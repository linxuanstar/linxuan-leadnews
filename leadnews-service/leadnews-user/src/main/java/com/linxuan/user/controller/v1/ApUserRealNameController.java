package com.linxuan.user.controller.v1;

import com.linxuan.common.constants.UserConstants;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.user.dtos.AuthDto;
import com.linxuan.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealNameController {

    @Autowired
    private ApUserRealnameService apUserRealnameService;

    /**
     * 查询APP端用户列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult loadListByStatus(@RequestBody AuthDto dto) {
        return apUserRealnameService.loadListByStatus(dto);
    }

    /**
     * 更新该用户状态 用户实名认证成功 可以成为自媒体端用户
     *
     * @param dto
     * @return
     */
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return apUserRealnameService.updateStatus(dto, UserConstants.PASS_AUTH);
    }

    /**
     * 用户失败
     *
     * @param dto
     * @return
     */
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return apUserRealnameService.updateStatus(dto, UserConstants.FAIL_AUTH);
    }
}
