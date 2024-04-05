package com.linxuan.article.feign;

import com.linxuan.article.service.ApArticleService;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    /**
     * article端保存图文信息
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }
}
