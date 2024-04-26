package com.linxuan.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.linxuan.article.service.ApCollectionService;
import com.linxuan.common.constants.BehaviorConstants;
import com.linxuan.common.redis.CacheService;
import com.linxuan.model.article.dtos.CollectionBehaviorDto;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        // 条件判断
        if (dto == null || dto.getEntryId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        String collectionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString());
        // 查询是否已收藏 如果已收藏直接报错 害怕前端瞎传参数
        if (StringUtils.isNotBlank(collectionJson) && dto.getOperation() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已收藏");
        }

        // 收藏
        if (dto.getOperation() == 0) {
            log.info("文章收藏，保存key:{},{},{}", dto.getEntryId(), user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hPut(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString(), JSON.toJSONString(dto));
        } else {
            // 取消收藏
            log.info("文章收藏，删除key:{},{},{}", dto.getEntryId(), user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hDelete(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}