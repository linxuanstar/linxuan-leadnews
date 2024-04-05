package com.linxuan.article.controller.v1;

import com.linxuan.article.service.ApArticleService;
import com.linxuan.common.constans.ArticleConstants;
import com.linxuan.model.article.dtos.ArticleHomeDto;
import com.linxuan.model.article.pojos.ApArticleContent;
import com.linxuan.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Autowired
    public ApArticleService apArticleService;

    /**
     * 加载首页
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    /**
     * 加载更多
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }


    /**
     * 加载最新
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }
}
