package com.linxuan.model.article.dtos;

import com.linxuan.model.article.pojos.ApArticle;
import lombok.Data;

@Data
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}