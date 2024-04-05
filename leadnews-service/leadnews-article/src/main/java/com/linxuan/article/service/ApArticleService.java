package com.linxuan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.article.dtos.ArticleHomeDto;
import com.linxuan.model.article.pojos.ApArticle;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     *
     * @param articleHomeDto
     * @param type           1是加载最新、2是加载更多
     * @return
     */
    public ResponseResult load(ArticleHomeDto articleHomeDto, short type);

    /**
     * 保存app端相关文章信息
     * @param dto
     * @return
     */
    public ResponseResult saveArticle(ArticleDto dto);
}
