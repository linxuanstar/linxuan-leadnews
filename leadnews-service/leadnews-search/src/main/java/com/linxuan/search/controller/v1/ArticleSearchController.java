package com.linxuan.search.controller.v1;

import com.linxuan.model.article.dtos.UserSearchDto;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.search.service.ArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/article/search")
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;

    /**
     * 调用文章搜索功能
     *
     * @param dto
     * @return
     * @throws IOException
     */
    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto dto) throws IOException {
        return articleSearchService.search(dto);
    }
}
