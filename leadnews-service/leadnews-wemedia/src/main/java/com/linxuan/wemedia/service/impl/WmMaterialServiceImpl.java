package com.linxuan.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.file.service.FileStorageService;
import com.linxuan.model.common.dtos.PageResponseResult;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.wemedia.dtos.WmMaterialDto;
import com.linxuan.model.wemedia.pojos.WmMaterial;
import com.linxuan.utils.thread.WmThreadLocalUtil;
import com.linxuan.wemedia.mapper.WmMaterialMapper;
import com.linxuan.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 检查参数合法性
        if (multipartFile.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 上传至minio
        // 获取文件参数
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileUrl = null;
        try {
            fileUrl = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}", fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 保存至DB
        WmMaterial wmMaterial = WmMaterial.builder()
                .userId(WmThreadLocalUtil.getUser().getId())
                .url(fileUrl)
                .type((short) 0)
                .isCollection((short) 0)
                .createdTime(new Date())
                .build();
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }

    /**
     * 查询图片列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        // 检查参数合法性
        dto.checkParam();

        // 分页查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果是已收藏，那么添加该条件
        if (dto.getIsCollection() != null && dto.getIsCollection() == 1) {
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }
        // 按照用户查询
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());
        // 按照时间降序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        page = page(page, lambdaQueryWrapper);

        // 返回结果
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;

    }
}
