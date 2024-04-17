package com.linxuan.feign.api.article.fallback;

import com.linxuan.feign.api.article.IArticleClient;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * feign远程调用app端saveArticle失败配置
 */
@Component
public class IArticleClientFallback implements IArticleClient {
    /**
     * 调用article端保存文章接口失败配置
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }

    @Override
    public ResponseResult findArticleConfigByArticleId(Long articleId) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "获取数据失败");
    }

}
