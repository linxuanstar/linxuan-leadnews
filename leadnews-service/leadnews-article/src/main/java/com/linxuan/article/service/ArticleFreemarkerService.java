package com.linxuan.article.service;

import com.linxuan.model.article.pojos.ApArticle;

public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minIO中
     *
     * @param apArticle 最后将生成的静态文件路径存储至apArticle.static_url字段
     * @param content   需要存储的内容
     */
    void buildArticleToMinIO(ApArticle apArticle, String content);
}
