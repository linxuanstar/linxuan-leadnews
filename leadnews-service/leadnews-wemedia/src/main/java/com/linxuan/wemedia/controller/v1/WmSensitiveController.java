package com.linxuan.wemedia.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.SensitiveDto;
import com.linxuan.model.wemedia.pojos.WmSensitive;
import com.linxuan.wemedia.service.WmSensitiveService;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    /**
     * 查询敏感词列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody SensitiveDto dto) {
        return wmSensitiveService.list(dto);
    }

    /**
     * 新增敏感词
     *
     * @param wmSensitive
     * @return
     */
    @PostMapping("/save")
    public ResponseResult insert(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.insert(wmSensitive);
    }

    /**
     * 更新敏感词
     *
     * @param wmSensitive
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.update(wmSensitive);
    }

    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") Integer id) {
        return wmSensitiveService.delete(id);
    }
}
