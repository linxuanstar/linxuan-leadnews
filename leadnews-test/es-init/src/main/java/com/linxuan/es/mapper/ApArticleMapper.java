package com.linxuan.es.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.es.pojo.SearchArticleVo;

public interface ApArticleMapper extends BaseMapper<SearchArticleVo> {
    /**
     * 搜索文章列表
     * @return 返回文章列表
     */
    List<SearchArticleVo> loadArticleList();
}
