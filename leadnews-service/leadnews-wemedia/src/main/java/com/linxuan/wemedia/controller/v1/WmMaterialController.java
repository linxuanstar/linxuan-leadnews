package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.wemedia.dtos.WmMaterialDto;
import com.linxuan.model.wemedia.pojos.WmMaterial;
import com.linxuan.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/material/")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 查询图片列表
     *
     * @param wmMaterialDto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto wmMaterialDto) {
        return wmMaterialService.findList(wmMaterialDto);
    }

    /**
     * 删除图片文件 并没有在minio中删除
     *
     * @param id
     * @return
     */
    @GetMapping("/del_picture/{id}")
    public ResponseResult delPicture(@PathVariable Integer id) {
        return wmMaterialService.delPicture(id);
    }

    /**
     * 收藏图片素材
     *
     * @param id
     * @return
     */
    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable Integer id) {
        return wmMaterialService.collect(id);
    }

    /**
     * 取消收藏图片素材
     *
     * @param id
     * @return
     */
    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable Integer id) {
        return wmMaterialService.cancelCollect(id);
    }
}
