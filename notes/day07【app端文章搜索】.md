# app端文章搜索功能

主要开发的功能有三大块：APP端文章搜索、APP端搜索历史记录、APP端联想词查询。使用的新技术有ES、Mongodb。

## Docker 部署ES环境

```shell
# 拉取ES镜像
[root@localhost ~]# docker pull elasticsearch:7.4.0
Digest: sha256:6765d5089eec04e1cc71c7020c15a553c6aa3845bed03c13aea59005ae011110
Status: Downloaded newer image for elasticsearch:7.4.0
docker.io/library/elasticsearch:7.4.0
# 创建容器
# -id:-i + -d参数结合，创建的容器一般称为守护式容器。-i是保持容器运行
[root@localhost ~]# docker run -id --name elasticsearch -d --restart=always -p 9200:9200 -p 9300:9300 -v /usr/share/elasticsearch/plugins:/usr/share/elasticsearch/plugins -e "discovery.type=single-node" elasticsearch:7.4.0
bb424719958fa34515f50a6d0ef245ddafcefe1d30be428e8bf11e04421c8a4c


# 配置中文分词器 ik。创建elasticsearch容器的时候，映射了目录，所以可以在宿主机上进行配置ik中文分词器
# 选择ik分词器的时候，需要与elasticsearch的版本好对应上。elasticsearch-analysis-ik-7.4.0.zip
[root@localhost ~]# cd /usr/share/elasticsearch/plugins
[root@localhost plugins]# mkdir analysis-ik
[root@localhost plugins]# cd analysis-ik
# filezilla上传elasticsearch-analysis-ik-7.4.0.zip文件至/opt/software/目录
[root@localhost analysis-ik]# ls /opt/software/
elasticsearch-analysis-ik-7.4.0.zip
# 将文件移动到/usr/share/elasticsearch/plugins/analysis-ik位置
[root@localhost analysis-ik]# mv /opt/software/elasticsearch-analysis-ik-7.4.0.zip /usr/share/elasticsearch/plugins/analysis-ik
[root@localhost analysis-ik]# ll
总用量 4400
-rw-r--r--. 1 root root 4504485 4月   9 16:19 elasticsearch-analysis-ik-7.4.0.zip
# 安装unzip插件
[root@localhost analysis-ik]# yum install -y unzip zip
# 解压文件
[root@localhost analysis-ik]# unzip elasticsearch-analysis-ik-7.4.0.zip
# 删除压缩包
[root@localhost analysis-ik]# rm -rf elasticsearch-analysis-ik-7.4.0.zip
[root@localhost analysis-ik]# ll
总用量 1432
-rw-r--r--. 1 root root 263965 5月   6 2018 commons-codec-1.9.jar
-rw-r--r--. 1 root root  61829 5月   6 2018 commons-logging-1.2.jar
drwxr-xr-x. 2 root root   4096 10月  7 2019 config
-rw-r--r--. 1 root root  54643 10月  7 2019 elasticsearch-analysis-ik-7.4.0.jar
-rw-r--r--. 1 root root 736658 5月   6 2018 httpclient-4.5.2.jar
-rw-r--r--. 1 root root 326724 5月   6 2018 httpcore-4.4.4.jar
-rw-r--r--. 1 root root   1805 10月  7 2019 plugin-descriptor.properties
-rw-r--r--. 1 root root    125 10月  7 2019 plugin-security.policy

# 重启ES容器
[root@localhost analysis-ik]# docker ps
CONTAINER ID   IMAGE                           NAMES
bb424719958f   elasticsearch:7.4.0             elasticsearch
[root@localhost analysis-ik]# docker restart bb424719958f
bb424719958f
```

使用 Postman 测试：发送[POST请求](http://192.168.88.129:9200/_analyze)，使用 raw 方式携带 JSON 格式参数

```json
{
    "analyzer": "ik_max_word",
    "text": "你好，我是林炫，发送请求"
}
```

## APP端文章搜索

APP端文章搜索要实现的功能如下：用户输入关键可搜索文章列表、关键词高亮显示、文章列表展示与 home 展示一样，当用户点击某一篇文章，可查看文章详情。

搜索结果页面展示内容：标题、布局（三封面图/单封面图）、封面图片URL、发布时间、作者名称、文章ID、作者ID、文章静态URL。标题和内容需要索引和分词，也就是用户根据标题和内容进行搜索。

为了加快检索的效率，在查询的时候不会直接从数据库中查询文章，需要在 elasticsearch 中进行高速检索。

### 创建索引库和映射

使用 Postman 发送 PUT 请求并携带映射参数，[创建索引库和映射](http://192.168.88.129:9200/app_info_article)。

```json
// PUT http://192.168.88.129:9200/app_info_article
{
    "mappings":{
        "properties":{
            "id":{
                "type":"long"
            },
            "publishTime":{
                "type":"date"
            },
            "layout":{
                "type":"integer"
            },
            "images":{
                "type":"keyword",
                "index": false
            },
            "staticUrl":{
                "type":"keyword",
                "index": false
            },
            "authorId": {
                "type": "long"
            },
            "authorName": {
                "type": "text"
            },
            "title":{
                "type":"text",
                "analyzer":"ik_smart"
            },
            "content":{
                "type":"text",
                "analyzer":"ik_smart"
            }
        }
    }
}
```

| 索引库操作     | 请求                                                         |
| -------------- | ------------------------------------------------------------ |
| 查询索引和映射 | GET请求：http://192.168.88.129:9200/app_info_article         |
| 删除索引及映射 | DELETE请求：http://192.168.88.129:9200/app_info_article      |
| 查询所有文档   | GET请求：http://192.168.88.129:9200/app_info_article/_search |

### 数据初始化到索引库

前面已经创建了一个索引库 app_info_article，接下来就是将文档信息导入到该索引库中。

#### 项目环境搭建

使用测试项目将数据初始化到索引库中，或者不适用也行，直接使用PostMan。该项目仅仅是用来导入数据及测试得，具体得搜索功能接下来实现。

```xml
<artifactId>es-init</artifactId>
<dependencies>
    <!-- 引入依赖模块 -->
    <dependency>
        <groupId>com.linxuan</groupId>
        <artifactId>leadnews-common</artifactId>
    </dependency>

    <!-- Spring boot starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!--elasticsearch-->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>7.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch</groupId>
        <artifactId>elasticsearch</artifactId>
        <version>7.4.0</version>
    </dependency>

    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.13.1</version>
    </dependency>
</dependencies>
</project>
```

```java
package com.linxuan.es;

@SpringBootApplication
@MapperScan("com.linxuan.es.mapper")
public class EsInitApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsInitApplication.class, args);
    }
}
```

```java
package com.linxuan.es.pojo;

@Data
public class SearchArticleVo {
    // 文章id
    private Long id;
    // 文章标题
    private String title;
    // 文章发布时间
    private Date publishTime;
    // 文章布局
    private Integer layout;
    // 封面
    private String images;
    // 作者id
    private Long authorId;
    // 作者名词
    private String authorName;
    // 静态url
    private String staticUrl;
    // 文章内容
    private String content;
}
```

```java
package com.linxuan.es.mapper;

public interface ApArticleMapper extends BaseMapper<SearchArticleVo> {
    /**
     * 搜索文章列表
     * @return 返回文章列表
     */
    List<SearchArticleVo> loadArticleList();
}
```

```java
package com.linxuan.es.config;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchConfig {
    private String host;
    private int port;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(host, port, "http")
        ));
    }
}
```

```yml
# application.yml添加如下配置
server:
  port: 9999
spring:
  application:
    name: es-article

  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_article?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.linxuan.model.article.pojos


#自定义elasticsearch连接配置
elasticsearch:
  host: 192.168.88.129
  port: 9200
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- ApArticleMapper.xml文件放在resources/mapper目录下面 -->
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linxuan.es.mapper.ApArticleMapper">

    <resultMap id="resultMap" type="com.linxuan.es.pojo.SearchArticleVo">
        <id column="id" property="id"/>
        <result column="title" property="title"/>
        <result column="author_id" property="authorId"/>
        <result column="author_name" property="authorName"/>
        <result column="layout" property="layout"/>
        <result column="images" property="images"/>
        <result column="publish_time" property="publishTime"/>
        <result column="static_url" property="staticUrl"/>
        <result column="content" property="content"/>
    </resultMap>
    <select id="loadArticleList" resultMap="resultMap">
        SELECT aa.*,
               aacon.content
        FROM `ap_article` aa,
             ap_article_config aac,
             ap_article_content aacon
        WHERE aa.id = aac.article_id
          AND aa.id = aacon.article_id
          AND aac.is_delete != 1
          AND aac.is_down != 1
    </select>
</mapper>
```

#### 文章信息导入索引库

| **MySQL** | **Elasticsearch** | **说明**                                                     |
| --------- | ----------------- | ------------------------------------------------------------ |
| Table     | Index             | 索引（Index），就是文档的集合，类似数据库的表（table）       |
| Row       | Document          | 文档（Document），就是一条条的数据，类似数据库中的行（Row），文档都是JSON格式 |
| Column    | Field             | 字段（Field），就是JSON文档中的字段，类似数据库中的列（Column） |
| Schema    | Mapping           | Mapping 是索引中文档的约束，例如字段类型约束。类似数据库的表结构（Schema） |
| SQL       | DSL               | DSL 是 elasticsearch 提供的JSON风格的请求语句，用来操作elasticsearch，实现CRUD |

```java
package com.linxuan.es;

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
```

### 文章搜索功能实现（--TODO）

#### 搭建基础环境

在 leadnews-service 模块下面创建 leadnews-search 子模块。

```xml
<parent>
    <groupId>com.linxuan</groupId>
    <artifactId>leadnews-service</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
<artifactId>leadnews-search</artifactId>

<dependencies>
    <!--elasticsearch-->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>7.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch</groupId>
        <artifactId>elasticsearch</artifactId>
        <version>7.4.0</version>
    </dependency>
</dependencies>
```

```java
package com.linxuan.search;

/**
 * @EnableDiscoveryClient: 配置注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
```

```java
package com.linxuan.search.config;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchConfig {
    private String host;
    private int port;

    @Bean
    public RestHighLevelClient client() {
        System.out.println(host);
        System.out.println(port);
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(host, port, "http")
        ));
    }
}
```

```yml
# leadnews-search模块下面创建bootstrap.yml文件
server:
  port: 51804
spring:
  application:
    name: leadnews-search
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.88.129:8848
      config:
        server-addr: 192.168.88.129:8848
        file-extension: yml
```

```yml
# 创建nacos配置中心 id = leadnews-search
spring:
  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
elasticsearch:
  host: 192.168.88.129
  port: 9200
```

因为这是APP文章端的搜索功能，所以肯定要经过APP端网关，因此在APP端网关的 nacos 中添加配置

```yml
#搜索微服务
- id: leadnews-search
 uri: lb://leadnews-search
 predicates:
   - Path=/search/**
 filters:
   - StripPrefix= 1
```

```yml
# APP端网关的 nacos 现在所有的配置
spring:
  cloud:
    gateway:
      # 全局的跨域处理
      globalcors:
        # 解决options请求被拦截问题
        add-to-simple-url-handler-mapping: true
        corsConfigurations:
          '[/**]':
            allowedHeaders: "*" # 允许在请求中携带的头信息
            allowedOrigins: "*" # 允许哪些网站的跨域请求
            allowedMethods:     # 允许的跨域ajax的请求方式
              - GET
              - POST
              - DELETE
              - PUT
              - OPTION
      # 路由平台管理配置
      routes:
        # 路由id，自定义，只要唯一即可
        - id: user
          # 路由的目标地址 lb就是负载均衡，后面跟服务名称
          uri: lb://leadnews-user
          # 路由断言，也就是判断请求是否符合路由规则的条件
          predicates:
            # 这个是按照路径匹配，只要以/user/开头就符合要求
            - Path=/user/**
          # 过滤器
          filters:
            # 去掉URL路径中的部分前缀，这里是去掉了user
            - StripPrefix=1
        # 文章管理
        - id: article
          uri: lb://leadnews-article
          predicates:
            - Path=/article/**
          filters:
            - StripPrefix=1
        #搜索微服务
        - id: leadnews-search
          uri: lb://leadnews-search
          predicates:
            - Path=/search/**
          filters:
            - StripPrefix= 1
```

#### 功能代码实现

|          | **说明**                      |
| -------- | ----------------------------- |
| 接口路径 | /api/v1/article/search/search |
| 请求方式 | POST                          |
| 参数     | UserSearchDto                 |
| 响应结果 | ResponseResult                |

```java
package com.linxuan.model.article.dtos;

@Data
public class UserSearchDto {

    /**
     * 搜索关键字
     */
    String searchWords;
    /**
     * 当前页
     */
    int pageNum;
    /**
     * 分页条数
     */
    int pageSize;
    /**
     * 最小时间
     */
    Date minBehotTime;

    /**
     * 获取当前页得起始索引
     * @return
     */
    public int getFromIndex() {
        if (this.pageNum < 1) return 0;
        if (this.pageSize < 1) this.pageSize = 10;
        return this.pageSize * (pageNum - 1);
    }
}
```

```java
package com.linxuan.search.service;

public interface ArticleSearchService {
    /**
     * ES 根据条件分页查询文章列表
     *
     * @param userSearchDto 条件
     * @return 返回分页查询结果
     * @throws IOException ES查询可能抛出IO异常
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}
```

```java
package com.linxuan.search.service.impl;

@Slf4j
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * ES 根据条件分页查询文章列表
     *
     * @param dto 条件
     * @return 返回分页查询结果
     * @throws IOException ES查询可能抛出IO异常
     */
    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {

        // 1.检查参数
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.设置查询条件
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
```

```java
package com.linxuan.search.controller.v1;

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
```

启动项目进行测试，启动文章微服务 leadnews-article，用户微服务 leadnews-user，搜索微服务 leadnews-search，app 网关微服务 leadnews-app-gateway，app 前端工程。在 APP 前端工程搜索文章，便成功显示。

### 文章自动审核构建索引

文章在审核成功之后，不仅需要添加到 MySQL 中，同时也应该添加到 ES 中。那么这里可以使用 Kafka 来传递消息，当文章审核成功后文章微服务端发送消息至 MQ，搜索微服务端接收消息，添加数据到索引库中。

#### 文章微服务发送消息

```java
package com.linxuan.model.search.vos;

/**
 * MQ 传递消息对象 包含文章信息。
 * 文章审核完毕之后传递文章信息，文章搜索段将该文章添加到索引库中。
 */
@Data
public class SearchArticleVo {

    // 文章id
    private Long id;
    // 文章标题
    private String title;
    // 文章发布时间
    private Date publishTime;
    // 文章布局
    private Integer layout;
    // 封面
    private String images;
    // 作者id
    private Long authorId;
    // 作者名词
    private String authorName;
    // 静态url
    private String staticUrl;
    // 文章内容
    private String content;
}
```

```java
package com.linxuan.article.service.impl;

@Slf4j
@Service
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 生成静态文件上传到minIO中，异步调用该方法
     *
     * @param apArticle 最后将生成的静态文件路径存储至apArticle.static_url字段
     * @param content   需要存储的内容
     */
    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        // 参数校验
        if (StringUtils.isNotBlank(content)) {
            // 文件输出流
            StringWriter out = new StringWriter();

            try {
                // 通过freemarker生成html文件 文件默认路径就是classpath:/templates
                // 创建模板对象
                Template template = configuration.getTemplate("article.ftl");
                // 创建数据模型
                Map<String, Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSON.parseArray(content));
                // 合成文件
                template.process(contentDataModel, out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 将html文件上传minio
            InputStream inputStream = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", 
                                                            apArticle.getId() + 
                                                            ".html", inputStream);

            // 修改ap_article表，保存static_url字段
            apArticleService.update(new LambdaUpdateWrapper<ApArticle>()
                    .eq(ApArticle::getId, apArticle.getId())
                    .set(ApArticle::getStaticUrl, path));

            // 添加该行
            // 向文章搜索端发送消息，文章搜索段接收到文章消息之后添加到索引库中
            createArticleESIndex(apArticle, content, path);
        }
    }


    /**
     * 送消息，创建索引
     *
     * @param apArticle 文章信息
     * @param content   文章内容详情
     * @param path      文章在minio中存储的链接
     */
    private void createArticleESIndex(ApArticle apArticle, String content, String path) {
        SearchArticleVo vo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, vo);
        vo.setContent(content);
        vo.setStaticUrl(path);

        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(vo));
    }
}
```

```java
package com.linxuan.common.constants;

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

    // 添加该常量
    // 文章微服务向搜索微服务传递消息的topic
    public static final String ARTICLE_ES_SYNC_TOPIC = "article.es.sync.topic";
}
```

```yaml
# 在文章微服务的nacos的配置中心添加如下配置
spring:
  kafka:
    bootstrap-servers: 192.168.88.129:9092
    producer:
      retries: 10
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

#### 搜索微服务接收并创建索引

```yaml
# 搜索微服务中添加kafka的配置,nacos配置如下
spring:
  kafka:
    bootstrap-servers: 192.168.88.129:9092
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

2.定义监听接收消息,保存索引数据

```java
package com.linxuan.search.listener;

@Component
@Slf4j
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message){
        if(StringUtils.isNotBlank(message)){

            log.info("SyncArticleListener,message={}",message);

            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
            IndexRequest indexRequest = new IndexRequest("app_info_article");
            indexRequest.id(searchArticleVo.getId().toString());
            indexRequest.source(message, XContentType.JSON);
            try {
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("sync es error={}",e);
            }
        }

    }
}
```

想要测试需要启动所有微服务

## APP端搜索记录功能

用户搜索展示搜索记录10条，按照搜索关键词的时间倒序。可以删除搜索记录。保存历史记录，保存10条，多余的则删除最久的历史记录。

用户的搜索记录，需要给每一个用户都保存一份，数据量较大，要求加载速度快，通常这样的数据存储到 mongodb更合适，不建议直接存储到关系型数据库中。

```sh
[root@localhost ~]# docker pull mongo
Using default tag: latest
latest: Pulling from library/mongo
Digest: sha256:5be752bc5f2ac4182252d0f15d74df080923aba39700905cb26d9f70f39e9702
Status: Downloaded newer image for mongo:latest
docker.io/library/mongo:latest
[root@localhost ~]# docker run -di --name mongo-service --restart=always -p 27017:27017 -v ~/data/mongodata:/data mongo
113f5025382131cb0cc0fa53eca374d72b29c2f4024c9c77bbb9b01ef3e81ff7
```

```sh
# 进入docker的mongo容器 注意只是进入容器等于进到了一个小系统 这个系统专门存储mongodb
[root@localhost ~]# docker exec -it mongo-service /bin/bash
root@113f50253821:/# 
# 通过mongod命令查看mongodb版本
root@113f50253821:/# mongod --version
db version v5.0.5
Build Info: {
    "version": "5.0.5",
    "gitVersion": "d65fd89df3fc039b5c55933c0f71d647a54510ae",
    "openSSLVersion": "OpenSSL 1.1.1f  31 Mar 2020",
    "modules": [],
    "allocator": "tcmalloc",
    "environment": {
        "distmod": "ubuntu2004",
        "distarch": "x86_64",
        "target_arch": "x86_64"
    }
}
# 进入mongo Shell端
root@113f50253821:/# mongo;
MongoDB shell version v5.0.5
connecting to: mongodb://127.0.0.1:27017/?compressors=disabled&gssapiServiceName=mongodb
Implicit session: session { "id" : UUID("cc369371-b989-4baf-85ac-477772eb1b92") }
MongoDB server version: 5.0.5
================
# 查看所有数据库
> show dbs;
admin   0.000GB
config  0.000GB
local   0.000GB
test    0.000GB
# 查看所有表
> show tables;
ap_user_search
```

```sh
# 使用本地的mongodb shell连接docker中mongodb
E:\MongoDB\bin>mongosh.exe 192.168.88.129
Current Mongosh Log ID: 661d512a36366bcf36eb3843
Connecting to:          mongodb://192.168.88.129:27017/?directConnection=true&appName=mongosh+1.8.2
Using MongoDB:          5.0.5
Using Mongosh:          1.8.2

For mongosh info see: https://docs.mongodb.com/mongodb-shell/

test> show tables;
ap_user_search
test>
```

### 创建测试项目

```sql
# MongoDB中导入SQL文件
use leadnews-history;
// ----------------------------
// Collection structure for ap_associate_words
// ----------------------------
db.getCollection("ap_associate_words").drop();
db.createCollection("ap_associate_words");

// ----------------------------
// Documents of ap_associate_words
// ----------------------------
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bd0043aab4f3022ce4a119"),
    associateWords: "黑马程序员",
    createdTime: ISODate("2021-06-06T17:05:07Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bd0060dcf502511cf7dd9d"),
    associateWords: "黑马头条",
    createdTime: ISODate("2021-06-06T17:05:36Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bd00746f1adf5bf6812e89"),
    associateWords: "黑马健康",
    createdTime: ISODate("2021-06-06T17:05:56Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bd008bacd61257dd1def70"),
    associateWords: "黑马java",
    createdTime: ISODate("2021-06-06T17:06:19Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbef32966c83665b0c412"),
    associateWords: "黑马",
    createdTime: ISODate("2021-06-07T06:38:43Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf00ba1ff738964402f5"),
    associateWords: "黑马王子",
    createdTime: ISODate("2021-06-07T06:38:56Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf0bf77cee6b3ab24088"),
    associateWords: "创业黑马",
    createdTime: ISODate("2021-06-07T06:39:07Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf16422e154b1460f209"),
    associateWords: "黑马股",
    createdTime: ISODate("2021-06-07T06:39:18Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf2c342fda48b9bd2d76"),
    associateWords: "黑马乐园",
    createdTime: ISODate("2021-06-07T06:39:39Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf3942b3ac4db5aa99a7"),
    associateWords: "黑马会",
    createdTime: ISODate("2021-06-07T06:39:53Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf4577a037353abc336d"),
    associateWords: "黑马自行车",
    createdTime: ISODate("2021-06-07T06:40:05Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf529a20a4616c2112c2"),
    associateWords: "黑马股票",
    createdTime: ISODate("2021-06-07T06:40:18Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf60e8d80a6e0513426a"),
    associateWords: "黑马影视",
    createdTime: ISODate("2021-06-07T06:40:32Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf6e0eee9919bdb422dd"),
    associateWords: "黑马论坛",
    createdTime: ISODate("2021-06-07T06:40:46Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf7be26ebc00ccbad249"),
    associateWords: "黑马训练营",
    createdTime: ISODate("2021-06-07T06:40:59Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf898b13451f8447f6d2"),
    associateWords: "黑马it",
    createdTime: ISODate("2021-06-07T06:41:13Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbf96795ea97bc80e7d82"),
    associateWords: "黑马培训",
    createdTime: ISODate("2021-06-07T06:41:25Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfa6715f926844b70b38"),
    associateWords: "黑马视频",
    createdTime: ISODate("2021-06-07T06:41:41Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfb5abf4de2c8466be79"),
    associateWords: "黑马java",
    createdTime: ISODate("2021-06-07T06:41:57Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfc1f2c2745b46b7f4a1"),
    associateWords: "黑马教程",
    createdTime: ISODate("2021-06-07T06:42:09Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfce31c9be6646b020e9"),
    associateWords: "黑马传智",
    createdTime: ISODate("2021-06-07T06:42:22Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfdb4ed4521b765f308d"),
    associateWords: "传智黑马",
    createdTime: ISODate("2021-06-07T06:42:35Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbfec550b7e7e2cd620a1"),
    associateWords: "北京黑马",
    createdTime: ISODate("2021-06-07T06:42:52Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdbffbc8cf631c9db0e571"),
    associateWords: "黑马教育",
    createdTime: ISODate("2021-06-07T06:43:07Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdc0074c59200e9c87486a"),
    associateWords: "黑马学院",
    createdTime: ISODate("2021-06-07T06:43:19Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
db.getCollection("ap_associate_words").insert([ {
    _id: ObjectId("60bdc02a4582bc47e1137a39"),
    associateWords: "黑马直播",
    createdTime: ISODate("2021-06-07T06:43:54Z"),
    _class: "com.linxuan.mongo.pojo.ApAssociateWords"
} ]);
```

```xml
<artifactId>mongodb-demo</artifactId>  
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
</dependencies>
```

```yaml
server:
  port: 9998
spring:
  data:
    mongodb:
      host: 192.168.88.129
      port: 27017
      database: leadnews-history
```

```java
package com.linxuan.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoDBApplication.class, args);
    }
}
```

```java
package com.linxuan.mongo.pojo;

/**
 * 联想词表
 */
@Data
@Document("ap_associate_words")
public class ApAssociateWords implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联想词
     */
    private String associateWords;

    /**
     * 创建时间
     */
    private Date createdTime;
}
```

```java
package com.linxuan.mongo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDBApplication.class)
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 查询一个
    @Test
    public void saveFindOne() {
        ApAssociateWords apAssociateWords = mongoTemplate
                .findById("60bdc02a4582bc47e1137a39", ApAssociateWords.class);
        System.out.println(apAssociateWords);
    }

    // 保存
    @Test
    public void saveTest() {
        ApAssociateWords apAssociateWords = new ApAssociateWords();
        apAssociateWords.setAssociateWords("林炫直播");
        apAssociateWords.setCreatedTime(new Date());
        mongoTemplate.save(apAssociateWords);
    }

    // 条件查询
    @Test
    public void testQuery() {
        Query query = Query.query(Criteria.where("associateWords").is("黑马头条"))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApAssociateWords> apAssociateWordsList = mongoTemplate.find(query, 
                                                                         ApAssociateWords.class);
        System.out.println(apAssociateWordsList);
    }

    @Test
    public void testDel() {
        mongoTemplate.remove(Query.query(Criteria.where("associateWords")
                .is("黑马头条")), ApAssociateWords.class);
    }
}
```

### 保存搜索记录

当用户在文章微服务端输入关键字之后我们记录关键字，然后存储 MongoDB 中最后显示出来。这其中要使用异步请求。

```sql
# MongoDB中导入SQL文件
use leadnews-history;

// ----------------------------
// Collection structure for ap_user_search
// ----------------------------
db.getCollection("ap_user_search").drop();
db.createCollection("ap_user_search");

// ----------------------------
// Documents of ap_user_search
// ----------------------------
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfad346ed51407504f131"),
    userId: NumberInt("4"),
    keyword: "黑马程序员",
    createdTime: ISODate("2021-06-07T07:13:17Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfad846ed51407504f132"),
    userId: NumberInt("4"),
    keyword: "黑马头条",
    createdTime: ISODate("2021-06-06T17:25:01Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfaf646ed51407504f135"),
    userId: NumberInt("4"),
    keyword: "日本",
    createdTime: ISODate("2021-06-06T17:21:52Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfafb46ed51407504f136"),
    userId: NumberInt("4"),
    keyword: "疫苗",
    createdTime: ISODate("2021-06-06T16:42:35Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfb0646ed51407504f137"),
    userId: NumberInt("4"),
    keyword: "搜索",
    createdTime: ISODate("2021-06-06T16:42:46Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfb0f46ed51407504f138"),
    userId: NumberInt("4"),
    keyword: "李世民",
    createdTime: ISODate("2021-06-06T16:42:55Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
db.getCollection("ap_user_search").insert([ {
    _id: ObjectId("60bcfe9c6b9b4f52b228b8ef"),
    userId: NumberInt("4"),
    keyword: "黑马",
    createdTime: ISODate("2021-06-06T16:58:04Z"),
    _class: "com.linxuan.search.pojos.ApUserSearch"
} ]);
```

#### 实现思路

用户输入关键字进行搜索之后，通过异步记录关键字。首先判断关键字是否存在，如果存在那么更新到最新时间。如果关键字不存在，判断总数据量是否超过10，不超过直接保存关键字，否则替换最后一条数据。

用户搜索记录对应的集合，对应实体类：

```java
package com.linxuan.search.pojos;

/**
 * 用户搜索信息表
 * 需要给每一个用户都保存一份，数据量较大，要求加载速度快，通常这样的数据存储到 mongodb
 * 因为在搜索模块导入了mongodb依赖，而实体类模块没有导入，所以直接在这里放实体类了
 */
@Data
@Document("ap_user_search")
public class ApUserSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 搜索词
     */
    private String keyword;

    /**
     * 创建时间
     */
    private Date createdTime;
}
```

```xml
<artifactId>leadnews-search</artifactId>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
</dependencies>
```

```yml
spring:
  data:
   mongodb:
    host: 192.168.88.129
    port: 27017
    database: leadnews-history
```

#### 实现步骤

```java
package com.linxuan.search.service;

public interface ApUserSearchService {
    /**
     * 保存用户搜索历史记录
     *
     * @param keyword 用户搜索的关键字
     * @param userId  用户ID
     */
    void insert(String keyword, Integer userId);
}
```

```java
package com.linxuan.search.service.impl;

@Slf4j
@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存用户搜索历史记录 异步调用
     *
     * @param keyword 用户搜索的关键字
     * @param userId  用户ID
     */
    @Async
    @Override
    public void insert(String keyword, Integer userId) {
        // 查询当前用户的搜索关键词是否存在
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);

        // 如果存在 更新创建时间
        if (apUserSearch != null) {
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
            return;
        }

        // 当前搜索的关键字不存在
        apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());


        Query query1 = Query.query(Criteria.where("userId").is(userId));
        query1.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApUserSearch> apUserSearchList = mongoTemplate.find(query1, ApUserSearch.class);

        // 判断当前历史记录总数量是否超过10，如果小于10直接相加，大于10替换最后一个
        if (apUserSearchList == null || apUserSearchList.size() < 10) {
            mongoTemplate.save(apUserSearch);
        } else {
            ApUserSearch lastUserSearch = apUserSearchList.get(apUserSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id")
                                                     .is(lastUserSearch.getId())), apUserSearch);
        }
    }
}
```

参考自媒体相关微服务，在搜索微服务中获取当前登录的用户。

1. 首先需要在APP端的网关，获取到用户的ID存入至请求头并重置请求。

   ```java
   package com.linxuan.app.gateway.filter;
   
   /**
    * 全局过滤器实现jwt校验
    * 设置该过滤器优先级为-1，值越小越先执行
    */
   @Slf4j
   @Order(-1)
   @Component
   public class AuthorizeFilter implements GlobalFilter {
   
       @Override
       public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
           // 获取参数
           ServerHttpRequest request = exchange.getRequest();
           ServerHttpResponse response = exchange.getResponse();
   
           // 登录操作放行，路由到相应微服务
           if (request.getURI().getPath().contains("/login")) {
               return chain.filter(exchange);
           }
   
           // 判断是否有token
           String token = request.getHeaders().getFirst("token");
           if (StringUtils.isEmpty(token)) {
               // 返回状态码校验失败
               response.setStatusCode(HttpStatus.UNAUTHORIZED);
               return response.setComplete();
           }
   
           // 判断token是否有效
           try {
               Claims claimsBody = AppJwtUtil.getClaimsBody(token);
               int result = AppJwtUtil.verifyToken(claimsBody);
               if (result == 1 || result == 2) {
                   // 返回状态码校验失败
                   response.setStatusCode(HttpStatus.UNAUTHORIZED);
                   return response.setComplete();
               }
   
               // ================添加下列代码==============
               // 获取用户ID
               Object userId = claimsBody.get("id");
               // 将用户ID存入请求头
               ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                   httpHeaders.add("userId", userId + "");
               }).build();
               // 重置请求
               exchange.mutate().request(serverHttpRequest).build();
           } catch (Exception e) {
               // 返回状态码校验失败
               response.setStatusCode(HttpStatus.UNAUTHORIZED);
               return response.setComplete();
           }
   
           return chain.filter(exchange);
       }
   }
   ```

2. 工具类模块创建APP端获取用户ID的工具类

   ```java
   package com.linxuan.utils.thread;
   
   public class AppThreadLocalUtil {
       private static final ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();
   
       /**
        * 添加用户
        *
        * @param user
        */
       public static void setUser(ApUser user) {
           AP_USER_THREAD_LOCAL.set(user);
       }
   
       /**
        * 获取用户
        *
        * @return
        */
       public static ApUser getUser() {
           return AP_USER_THREAD_LOCAL.get();
       }
   
       /**
        * 清理
        */
       public static void clear() {
           AP_USER_THREAD_LOCAL.remove();
       }
   }
   ```

3. APP端的搜索微服务实现拦截器，拦截所有请求获取用户ID并存入当前线程。

   ```java
   package com.linxuan.search.interceptor;
   
   @Component
   public class AppTokenInterceptor implements HandlerInterceptor {
   
       /**
        * 获取请求头中的userId信息并存入当前线程
        *
        * @param request
        * @param response
        * @param handler
        * @return
        */
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                                Object handler) {
           String userId = request.getHeader("userId");
           if (userId != null) {
               ApUser apUser = new ApUser();
               apUser.setId(Integer.parseInt(userId));
               AppThreadLocalUtil.setUser(apUser);
           }
           return true;
       }
   
       /**
        * 清理线程中数据,如果是postHandle可能会导致抛出异常导致无法清理
        *
        * @param request
        * @param response
        * @param handler
        * @param modelAndView
        * @throws Exception
        */
       @Override
       public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
           // AppThreadLocalUtil.clear();
       }
   
       /**
        * 清理线程中数据
        *
        * @param request
        * @param response
        * @param handler
        * @param ex
        * @throws Exception
        */
       @Override
       public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
           AppThreadLocalUtil.clear();
       }
   }
   ```

4. 让当前拦截器生效。

   ```java
   package com.linxuan.search.config;
   
   /**
    * 配置使拦截器生效，拦截所有的请求
    */
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
   
       @Autowired
       private AppTokenInterceptor requestUrlInterceptor;
   
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(requestUrlInterceptor).addPathPatterns("/**");
       }
   }
   ```

在 ArticleSearchService 的 search 方法中调用保存历史记录

```java
package com.linxuan.search.service.impl;

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

        // =================添加下列代码================
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
```

```java
package com.linxuan.search;

/**
 * @EnableDiscoveryClient: 配置注册中心
 */
@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
```

以此打开前端项目：leadnews-app-gateway APP端网关、leadnews-article APP端文章微服务、leadnews-user APP端登录用户微服务、leadnews-search APP端搜索微服务，以及通过 nginx 打开前端项目，测试搜索是否保存搜索记录。



### 4.5) 加载搜索记录列表

#### 4.5.1) 思路分析

按照当前用户，按照时间倒序查询

|          | **说明**             |
| -------- | -------------------- |
| 接口路径 | /api/v1/history/load |
| 请求方式 | POST                 |
| 参数     | 无                   |
| 响应结果 | ResponseResult       |

#### 4.5.2) 接口定义

```java
/**
 * <p>
 * APP用户搜索信息表 前端控制器
 * </p>
 *
 * @author linxuan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController{


    @PostMapping("/load")
    @Override
    public ResponseResult findUserSearch() {
        return null;
    }

}
```

#### 4.5.3) mapper

已定义

#### 4.5.4) 业务层

在ApUserSearchService中新增方法

```java
/**
     查询搜索历史
     @return
     */
ResponseResult findUserSearch();
```

实现方法

```java
 /**
     * 查询搜索历史
     *
     * @return
     */
@Override
public ResponseResult findUserSearch() {
    //获取当前用户
    ApUser user = AppThreadLocalUtil.getUser();
    if(user == null){
        return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
    }

    //根据用户查询数据，按照时间倒序
    List<ApUserSearch> apUserSearches = mongoTemplate.find(Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
    return ResponseResult.okResult(apUserSearches);
}
```

#### 4.5.5) 控制器

```java
/**
 * <p>
 * APP用户搜索信息表 前端控制器
 * </p>
 * @author linxuan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController{

    @Autowired
    private ApUserSearchService apUserSearchService;

    @PostMapping("/load")
    public ResponseResult findUserSearch() {
        return apUserSearchService.findUserSearch();
    }

}
```

#### 4.5.6) 测试

打开app的搜索页面，可以查看搜索记录列表

### 4.6) 删除搜索记录

#### 4.6.1) 思路分析

按照搜索历史id删除

|          | **说明**            |
| -------- | ------------------- |
| 接口路径 | /api/v1/history/del |
| 请求方式 | POST                |
| 参数     | HistorySearchDto    |
| 响应结果 | ResponseResult      |

#### 4.6.2) 接口定义

在ApUserSearchController接口新增方法

```java
@PostMapping("/del")
public ResponseResult delUserSearch(@RequestBody HistorySearchDto historySearchDto) {
    return null;
}
```

HistorySearchDto

```java
@Data
public class HistorySearchDto {
    /**
    * 接收搜索历史记录id
    */
    String id;
}
```

#### 4.6.3) 业务层

在ApUserSearchService中新增方法

```java
 /**
     删除搜索历史
     @param historySearchDto
     @return
     */
ResponseResult delUserSearch(HistorySearchDto historySearchDto);
```

实现方法

```java
/**
     * 删除历史记录
     *
     * @param dto
     * @return
     */
@Override
public ResponseResult delUserSearch(HistorySearchDto dto) {
    //1.检查参数
    if(dto.getId() == null){
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    //2.判断是否登录
    ApUser user = AppThreadLocalUtil.getUser();
    if(user == null){
        return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
    }

    //3.删除
    mongoTemplate.remove(Query.query(Criteria.where("userId").is(user.getId()).and("id").is(dto.getId())),ApUserSearch.class);
    return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
}
```

#### 4.6.4) 控制器

修改ApUserSearchController，补全方法

```java
@PostMapping("/del")
public ResponseResult delUserSearch(@RequestBody HistorySearchDto historySearchDto) {
    return apUserSearchService.delUserSearch(historySearchDto);
}
```

#### 4.6.5) 测试

打开app可以删除搜索记录

## 关键字联想词（删除功能）

### 5.1 需求分析

![1587366921085](D:\Java\IdeaProjects\lead_news\!information\day07\讲义\app端文章搜索.assets\1587366921085.png)

- 根据用户输入的关键字展示联想词

对应实体类

```java
package com.linxuan.search.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联想词表
 * </p>
 *
 * @author linxuan
 */
@Data
@Document("ap_associate_words")
public class ApAssociateWords implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联想词
     */
    private String associateWords;

    /**
     * 创建时间
     */
    private Date createdTime;

}
```

### 5.2)搜索词-数据来源

通常是网上搜索频率比较高的一些词，通常在企业中有两部分来源：

第一：自己维护搜索词

通过分析用户搜索频率较高的词，按照排名作为搜索词

第二：第三方获取

关键词规划师（百度）、5118、爱站网

![image-20210709160036983](D:\Java\IdeaProjects\lead_news\!information\day07\讲义\app端文章搜索.assets\image-20210709160036983.png)

导入资料中的ap_associate_words.js脚本到mongo中

### 5.3 功能实现

#### 5.3.1 接口定义

|          | **说明**                 |
| -------- | ------------------------ |
| 接口路径 | /api/v1/associate/search |
| 请求方式 | POST                     |
| 参数     | UserSearchDto            |
| 响应结果 | ResponseResult           |

新建接口

```java
package com.linxuan.search.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.search.dtos.UserSearchDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/associate")
public class ApAssociateWordsController {


    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto userSearchDto) {
        return null;
    }
}

```

#### 5.3.3 业务层

新建联想词业务层接口

```java
package com.linxuan.search.service;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.search.dtos.UserSearchDto;

/**
 * <p>
 * 联想词表 服务类
 * </p>
 *
 * @author linxuan
 */
public interface ApAssociateWordsService {

    /**
     联想词
     @param userSearchDto
     @return
     */
    ResponseResult findAssociate(UserSearchDto userSearchDto);

}
```

实现类

```java
package com.linxuan.search.service.impl;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.search.dtos.UserSearchDto;
import com.linxuan.search.pojos.ApAssociateWords;
import com.linxuan.search.service.ApAssociateWordsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Version: V1.0
 */
@Service
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 联想词
     * @param userSearchDto
     * @return
     */
    @Override
    public ResponseResult findAssociate(UserSearchDto userSearchDto) {
        //1 参数检查
        if(userSearchDto == null || StringUtils.isBlank(userSearchDto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页检查
        if (userSearchDto.getPageSize() > 20) {
            userSearchDto.setPageSize(20);
        }

        //3 执行查询 模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + userSearchDto.getSearchWords() + ".*"));
        query.limit(userSearchDto.getPageSize());
        List<ApAssociateWords> wordsList = mongoTemplate.find(query, ApAssociateWords.class);

        return ResponseResult.okResult(wordsList);
    }
}
```

#### 5.3.4  控制器

新建联想词控制器

```java
package com.linxuan.search.controller.v1;

import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.search.dtos.UserSearchDto;
import com.linxuan.search.service.ApAssociateWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 联想词表 前端控制器
 * </p>
 * @author linxuan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/associate")
public class ApAssociateWordsController{

    @Autowired
    private ApAssociateWordsService apAssociateWordsService;

    @PostMapping("/search")
    public ResponseResult findAssociate(@RequestBody UserSearchDto userSearchDto) {
        return apAssociateWordsService.findAssociate(userSearchDto);
    }
}
```

#### 5.3.5 测试

同样，打开前端联调测试效果



