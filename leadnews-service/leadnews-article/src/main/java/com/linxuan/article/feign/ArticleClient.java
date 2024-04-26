package com.linxuan.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.linxuan.article.service.ApArticleConfigService;
import com.linxuan.article.service.ApArticleService;
import com.linxuan.feign.api.article.IArticleClient;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.article.pojos.ApArticleConfig;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    /**
     * article端保存图文信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

    /**
     * 根据文章ID查询文章配置信息
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult findArticleConfigByArticleId(Long articleId) {
        ApArticleConfig apArticleConfig = apArticleConfigService.getOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, articleId));
        return ResponseResult.okResult(apArticleConfig);
    }
}
