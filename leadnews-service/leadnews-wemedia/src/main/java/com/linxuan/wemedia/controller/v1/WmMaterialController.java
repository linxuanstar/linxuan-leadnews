package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.WmMaterialDto;
import com.linxuan.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/material/")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 查询图片列表
     * @param wmMaterialDto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto wmMaterialDto) {
        return wmMaterialService.findList(wmMaterialDto);
    }
}
