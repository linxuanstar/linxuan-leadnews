package com.linxuan.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.linxuan.model.article.dtos.UserSearchDto;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.search.service.ApUserSearchService;
import com.linxuan.search.service.ArticleSearchService;
import com.linxuan.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * ES 根据条件分页查询文章列表
     *
     * @param dto 条件
     * @return 返回分页查询结果
     * @throws IOException ES查询可能抛出IO异常
     */
    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {

        // 检查参数
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();

        // 异步调用 保存搜索记录
        if (user != null && dto.getFromIndex() == 0) {
            apUserSearchService.insert(dto.getSearchWords(), user.getId());
        }


        // 设置查询条件
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 关键字的分词之后查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders
                .queryStringQuery(dto.getSearchWords())
                .field("title").field("content")
                .defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder);

        // 查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime")
                .lt(dto.getMinBehotTime().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);

        // 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(dto.getPageSize());

        // 按照发布时间倒序查询
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        // 设置高亮  title
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


        // 3.结果封装返回

        List<Map> list = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Map map = JSON.parseObject(json, Map.class);
            // 处理高亮
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0) {
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StringUtils.join(titles);
                // 高亮标题
                map.put("h_title", title);
            } else {
                // 原始标题
                map.put("h_title", map.get("title"));
            }
            list.add(map);
        }

        return ResponseResult.okResult(list);

    }
}
