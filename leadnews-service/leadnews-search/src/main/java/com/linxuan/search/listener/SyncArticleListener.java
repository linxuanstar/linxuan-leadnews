package com.linxuan.search.listener;

import com.alibaba.fastjson.JSON;
import com.linxuan.common.constans.ArticleConstants;
import com.linxuan.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 当文章为服务端向kafka的ARTICLE_ES_SYNC_TOPIC发送消息时会监听并获取
     * 最后将数据上传至kafka的索引库中
     *
     * @param message 监听到的数据
     */
    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) {
        if (StringUtils.isNotBlank(message)) {

            log.info("SyncArticleListener,message={}", message);

            // 转换接收到的信息
            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
            // 准备请求对象
            IndexRequest indexRequest = new IndexRequest("app_info_article");
            // 创建文档ID
            indexRequest.id(searchArticleVo.getId().toString());
            // 准备JSON文档
            indexRequest.source(message, XContentType.JSON);
            try {
                // 发送请求
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("sync es error={}", e);
            }
        }

    }
}