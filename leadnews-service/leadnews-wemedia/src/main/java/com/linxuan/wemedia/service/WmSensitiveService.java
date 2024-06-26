package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.SensitiveDto;
import com.linxuan.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * 查询
     * @param dto
     * @return
     */
    ResponseResult list(SensitiveDto dto);

    /**
     * 新增
     * @param wmSensitive
     * @return
     */
    ResponseResult insert(WmSensitive wmSensitive);

    /**
     * 修改
     * @param wmSensitive
     * @return
     */
    ResponseResult update(WmSensitive wmSensitive);

    /**
     * 删除
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);
}
