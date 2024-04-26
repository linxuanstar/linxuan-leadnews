package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.WmMaterialDto;
import com.linxuan.model.wemedia.pojos.WmMaterial;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 查询图片列表
     *
     * @param wmMaterialDto
     * @return
     */
    public ResponseResult findList(WmMaterialDto wmMaterialDto);

    /**
     * 删除图片文件 有局限性 查询是否有文章引用 并没有删除minio中
     * @param id
     * @return
     */
    ResponseResult delPicture(Integer id);

    /**
     * 收藏图片文件
     * @param id
     * @return
     */
    ResponseResult collect(Integer id);

    /**
     * 取消收藏图片素材
     * @param id
     * @return
     */
    ResponseResult cancelCollect(Integer id);
}
