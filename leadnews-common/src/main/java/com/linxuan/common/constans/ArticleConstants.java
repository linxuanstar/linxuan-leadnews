package com.linxuan.common.constans;

public class ArticleConstants {
    // 默认分页条数
    public static final Short DEFAULT_PAGE_SIZE = 10;
    // 分页条数最大值
    public static final Short MAX_PAGE_SIZE = 50;
    // 加载更多
    public static final Short LOADTYPE_LOAD_MORE = 1;
    // 加载最新
    public static final Short LOADTYPE_LOAD_NEW = 2;
    // 默认频道是推荐
    public static final String DEFAULT_TAG = "__all__";

    // 文章微服务向搜索微服务传递消息的topic
    public static final String ARTICLE_ES_SYNC_TOPIC = "article.es.sync.topic";

    public static final Integer HOT_ARTICLE_LIKE_WEIGHT = 3;
    public static final Integer HOT_ARTICLE_COMMENT_WEIGHT = 5;
    public static final Integer HOT_ARTICLE_COLLECTION_WEIGHT = 8;

    public static final String HOT_ARTICLE_FIRST_PAGE = "hot_article_first_page_";
}