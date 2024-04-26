package com.linxuan.article.controller.v1;

import com.linxuan.article.service.ApArticleService;
import com.linxuan.model.article.dtos.ArticleInfoDto;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleInfoController {

    @Autowired
    private ApArticleService apArticleService;

    /**
     * 加载文章所有行为 点赞、收藏、阅读次数等等
     * 之所以放在这里因为全路径为/api/v1/article/load_article_behavior
     *
     * @param dto
     * @return
     */
    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto dto) {
        return apArticleService.loadArticleBehavior(dto);
    }
}