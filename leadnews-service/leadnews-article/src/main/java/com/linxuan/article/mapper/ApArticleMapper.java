package com.linxuan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.model.article.dtos.ArticleHomeDto;
import com.linxuan.model.article.pojos.ApArticle;
import org.apache.commons.net.nntp.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 查询文章列表
     * @param articleHomeDto
     * @param type 1是加载最新、2是加载更多
     * @return
     */
    public List<Article> loadArticleList(ArticleHomeDto articleHomeDto, short type);
}
