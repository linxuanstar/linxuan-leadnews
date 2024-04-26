package com.linxuan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.article.pojos.ApArticleConfig;
import com.linxuan.model.comment.dtos.CommentConfigDto;
import com.linxuan.model.common.dtos.ResponseResult;

import java.util.Map;

public interface ApArticleConfigService extends IService<ApArticleConfig> {

    /**
     * 修改文章配置
     *
     * @param map
     */
    public void updateByMap(Map map);

    /**
     * 修改文章评论状态
     * @return
     */
    public ResponseResult updateCommentStatus(CommentConfigDto dto);
}