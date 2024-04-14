package com.linxuan.es;

import com.alibaba.fastjson.JSON;
import com.linxuan.es.mapper.ApArticleMapper;
import com.linxuan.es.pojo.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsInitApplication.class)
public class ApArticleTest {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 将文章数据添加到app_info_article索引库中
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        // 从数据库中查询文章列表
        List<SearchArticleVo> searchArticleVos = apArticleMapper.loadArticleList();

        // 创建ES批量导入数据请求
        BulkRequest bulkRequest = new BulkRequest("app_info_article");
        for (SearchArticleVo searchArticleVo : searchArticleVos) {
            // 创建新增文档的Request对象
            IndexRequest indexRequest = new IndexRequest()
                    .id(searchArticleVo.getId().toString())
                    .source(JSON.toJSONString(searchArticleVo), XContentType.JSON);
            // 将新增文档请求对象添加进批量导入对象中
            bulkRequest.add(indexRequest);
        }

        // 发送请求
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 查询app_info_article中所有得文档信息 就是之前添加进去得文章数据
     * @throws IOException
     */
    @Test
    public void testSearchAll() throws IOException {
        // 准备Request对象
        SearchRequest request = new SearchRequest("app_info_article");
        // 组织DSL参数
        request.source().query(QueryBuilders.matchAllQuery());
        // 发送请求 得到相应结果
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 解析相应结果
        SearchHits hits = response.getHits();
        // 得到查询的总条数
        long value = hits.getTotalHits().value;
        System.out.println("一共打印了" + value + "条数据");
        // 查询的结果数组
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            // 得到source
            String source = searchHit.getSourceAsString();
            System.out.println(source);
        }
    }
}
