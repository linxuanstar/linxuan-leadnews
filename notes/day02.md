# 文章列表功能

## 列表查询

需求：

1. 在默认频道展示10条文章信息
2. 切换频道查看不同种类文章
3. 用户下拉可以加载最新的文章（分页）本页文章列表中发布时间为最大的时间为依据
4. 用户上拉可以加载更多的文章信息（按照发布时间）本页文章列表中发布时间最小的时间为依据
5. 如果是当前频道的首页，前端传递默认参数：
   * maxBehotTime：0（毫秒）
   * minBehotTime：20000000000000（毫秒）--->2063年

#### 导入数据库

导入 leadnews_article.sql 文件

| **表名称**         | **说明**                     |
| ------------------ | ---------------------------- |
| ap_article         | 文章信息表，存储已发布的文章 |
| ap_article_config  | APP已发布文章配置表          |
| ap_article_content | APP已发布文章内容表          |
| ap_author          | APP文章作者信息表            |
| ap_collection      | APP收藏信息表                |

#### 导入实体类

```java
package com.linxuan.model.article.pojos;

/**
 * <p>
 * 文章信息表，存储已发布的文章
 * </p>
 */

@Data
@TableName("ap_article")
public class ApArticle implements Serializable {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;


    /**
     * 标题
     */
    private String title;

    /**
     * 作者id
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者名称
     */
    @TableField("author_name")
    private String authorName;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 频道名称
     */
    @TableField("channel_name")
    private String channelName;

    /**
     * 文章布局  0 无图文章   1 单图文章    2 多图文章
     */
    private Short layout;

    /**
     * 文章标记  0 普通文章   1 热点文章   2 置顶文章   3 精品文章   4 大V 文章
     */
    private Byte flag;

    /**
     * 文章封面图片 多张逗号分隔
     */
    private String images;

    /**
     * 标签
     */
    private String labels;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 收藏数量
     */
    private Integer collection;

    /**
     * 评论数量
     */
    private Integer comment;

    /**
     * 阅读数量
     */
    private Integer views;

    /**
     * 省市
     */
    @TableField("province_id")
    private Integer provinceId;

    /**
     * 市区
     */
    @TableField("city_id")
    private Integer cityId;

    /**
     * 区县
     */
    @TableField("county_id")
    private Integer countyId;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 同步状态
     */
    @TableField("sync_status")
    private Boolean syncStatus;

    /**
     * 来源
     */
    private Boolean origin;

    /**
     * 静态页面地址
     */
    @TableField("static_url")
    private String staticUrl;
}
```

```java
package com.linxuan.model.article.pojos;

/**
 * <p>
 * APP已发布文章配置表
 * </p>
 *
 */

@Data
@TableName("ap_article_config")
public class ApArticleConfig implements Serializable {

    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 是否可评论
     * true: 可以评论   1
     * false: 不可评论  0
     */
    @TableField("is_comment")
    private Boolean isComment;

    /**
     * 是否转发
     * true: 可以转发   1
     * false: 不可转发  0
     */
    @TableField("is_forward")
    private Boolean isForward;

    /**
     * 是否下架
     * true: 下架   1
     * false: 没有下架  0
     */
    @TableField("is_down")
    private Boolean isDown;

    /**
     * 是否已删除
     * true: 删除   1
     * false: 没有删除  0
     */
    @TableField("is_delete")
    private Boolean isDelete;
}
```

```java
package com.linxuan.model.article.pojos;

@Data
@TableName("ap_article_content")
public class ApArticleContent implements Serializable {

    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 文章内容
     */
    private String content;
}
```

#### 定义接口

|          | **加载首页**         | **加载更多**             | **加载最新**            |
| -------- | -------------------- | ------------------------ | ----------------------- |
| 接口路径 | /api/v1/article/load | /api/v1/article/loadmore | /api/v1/article/loadnew |
| 请求方式 | POST                 | POST                     | POST                    |
| 参数     | ArticleHomeDto       | ArticleHomeDto           | ArticleHomeDto          |
| 响应结果 | ResponseResult       | ResponseResult           | ResponseResult  ****    |

```java
package com.heima.model.article.dtos;

@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}
```

#### 基础配置

在 leadnews-service 下创建 leadnews-article 模块，

```xml
<!-- leadnews-servicepom.xml文件中添加子模块 -->
<modules>
    <module>leadnews-user</module>
    <module>leadnews-article</module>
</modules>
```

```java
package com.linxuan.article;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.linxuan.article.mapper")
public class ArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class,args);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

```yml
server:
  port: 51802
spring:
  application:
    name: leadnews-article
  #忽略Redis配置类，之后再使用
  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.88.129:8848
      config:
        server-addr: 192.168.88.129:8848
        file-extension: yml
```

```yml
# nacos中添加配置，名称为leadnews-article
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_article?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.linxuan.model.article.pojos
```

#### 功能开发

**编写mapper文件**

```java
package com.linxuan.article.mapper;

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
```

resources中新建mapper/ApArticleMapper.xml，配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linxuan.article.mapper.ApArticleMapper">

    <resultMap id="resultMap" type="com.linxuan.model.article.pojos.ApArticle">
        <id column="id" property="id"/>
        <result column="title" property="title"/>
        <result column="author_id" property="authorId"/>
        <result column="author_name" property="authorName"/>
        <result column="channel_id" property="channelId"/>
        <result column="channel_name" property="channelName"/>
        <result column="layout" property="layout"/>
        <result column="flag" property="flag"/>
        <result column="images" property="images"/>
        <result column="labels" property="labels"/>
        <result column="likes" property="likes"/>
        <result column="collection" property="collection"/>
        <result column="comment" property="comment"/>
        <result column="views" property="views"/>
        <result column="province_id" property="provinceId"/>
        <result column="city_id" property="cityId"/>
        <result column="county_id" property="countyId"/>
        <result column="created_time" property="createdTime"/>
        <result column="publish_time" property="publishTime"/>
        <result column="sync_status" property="syncStatus"/>
        <result column="static_url" property="staticUrl"/>
    </resultMap>
    <select id="loadArticleList" resultMap="resultMap">
        SELECT
        aa.*
        FROM
        `ap_article` aa
        LEFT JOIN ap_article_config aac ON aa.id = aac.article_id
        <where>
            and aac.is_delete != 1
            and aac.is_down != 1
            <!-- loadmore -->
            <if test="type != null and type == 1">
                and aa.publish_time <![CDATA[<]]> #{articleHomeDto.minBehotTime}
            </if>
            <if test="type != null and type == 2">
                and aa.publish_time <![CDATA[>]]> #{articleHomeDto.maxBehotTime}
            </if>
            <if test="articleHomeDto.tag != '__all__'">
                and aa.channel_id = #{articleHomeDto.tag}
            </if>
        </where>
        order by aa.publish_time desc
        limit #{articleHomeDto.size}
    </select>
</mapper>
```

**定义常量类**

```java
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
}
```

**业务层代码**

```java
package com.linxuan.article.service;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param articleHomeDto
     * @param type 1是加载最新、2是加载更多
     * @return
     */
    public ResponseResult load(ArticleHomeDto articleHomeDto, short type);
}
```

```java
package com.linxuan.article.service.impl;

@Slf4j
@Service
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper articleMapper;

    /**
     * 加载文章列表
     *
     * @param articleHomeDto
     * @param type           1是加载最新、2是加载更多
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto articleHomeDto, short type) {
        // 参数校验
        // 校验分页条数
        Integer size = articleHomeDto.getSize();
        if (size == null || size == 0) {
            size = (int) ArticleConstants.DEFAULT_PAGE_SIZE;
        }
        size = Math.min(size, ArticleConstants.MAX_PAGE_SIZE);
        // 校验加载类型
        if (type != ArticleConstants.LOADTYPE_LOAD_MORE && type != ArticleConstants.LOADTYPE_LOAD_NEW) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        // 校验频道ID
        if (StringUtils.isBlank(articleHomeDto.getTag())) {
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        // 校验时间
        if (articleHomeDto.getMaxBehotTime() == null)
            articleHomeDto.setMaxBehotTime(new Date());
        if (articleHomeDto.getMinBehotTime() == null)
            articleHomeDto.setMinBehotTime(new Date());

        // 加载文章列表
        List<Article> articles = articleMapper.loadArticleList(articleHomeDto, type);

        return ResponseResult.okResult(articles);
    }
}
```

**控制器代码**

```java
package com.linxuan.article.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Autowired
    public ApArticleService apArticleService;

    /**
     * 加载首页
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    /**
     * 加载更多
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }


    /**
     * 加载最新
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }
}
```

**nacos网关配置添加路由**

```yml
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
        - id: article
          uri: lb://leadnews-article
          predicates:
            - Path=/article/**
          filters:
            - StripPrefix=1
```

启动 Nginx [访问测试](http://localhost:8801/)

## 文章详情展示

实现过程如下：

1. 根据文章内容通过模板技术 freemarker 生成静态的 html 文件
2. 文件存入分布式文件系统 minIO 中并生成 html 访问路径
3. 获取 html url 路径进行文章详情展示

### 基础环境搭建

```xml
<artifactId>leadnews-article</artifactId>
<dependencies>
    <!-- freemarker依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-freemarker</artifactId>
    </dependency>
    <!-- 封装的minio依赖 -->
    <dependency>
        <groupId>com.linxuan.file</groupId>
        <artifactId>file-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

```yml
# 在leadnews-article模块的yml配置文件中添加minio配置，可以添加进application.yml、bootstrap.yml
# 这里将其添加进了Nacos的配置里面
minio:
  accessKey: minio
  secretKey: minio123
  bucket: leadnews
  endpoint: http://192.168.88.129:9000
  readPath: http://192.168.88.129:9000
```

```html
<!-- article.ftl 存放至项目resources/templates目录下 -->
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, viewport-fit=cover">
    <title>头条项目</title>
    <!-- 引入样式文件 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vant@2.12.20/lib/index.css">
    <!-- 页面样式 -->
    <!-- 这是一个css样式文件，将其存放至了minio的plugins/css/index.css目录下面 -->
    <!-- 该模板文件通过内容生成html文件后，会将html文件放到minio中，最后手机访问html链接 -->
    <link rel="stylesheet" href="../../../plugins/css/index.css">
</head>

<body>
<div id="app">
    <div class="article">
        <van-row>
            <van-col span="24" class="article-title" v-html="title"></van-col>
        </van-row>

        <van-row type="flex" align="center" class="article-header">
            <van-col span="3">
                <van-image round class="article-avatar" src="https://p3.pstatp.com/thumb/1480/7186611868"></van-image>
            </van-col>
            <van-col span="16">
                <div v-html="authorName"></div>
                <div>{{ publishTime | timestampToDateTime }}</div>
            </van-col>
            <van-col span="5">
                <van-button round :icon="relation.isfollow ? '' : 'plus'" type="info" class="article-focus"
                            :text="relation.isfollow ? '取消关注' : '关注'" :loading="followLoading" @click="handleClickArticleFollow">
                </van-button>
            </van-col>
        </van-row>

        <van-row class="article-content">
            <#if content??>
                <#list content as item>
                    <#if item.type='text'>
                        <van-col span="24" class="article-text">${item.value}</van-col>
                    <#else>
                        <van-col span="24" class="article-image">
                            <van-image width="100%" src="${item.value}"></van-image>
                        </van-col>
                    </#if>
                </#list>
            </#if>
        </van-row>

        <van-row type="flex" justify="center" class="article-action">
            <van-col>
                <van-button round :icon="relation.islike ? 'good-job' : 'good-job-o'" class="article-like"
                            :loading="likeLoading" :text="relation.islike ? '取消赞' : '点赞'" @click="handleClickArticleLike"></van-button>
                <van-button round :icon="relation.isunlike ? 'delete' : 'delete-o'" class="article-unlike"
                            :loading="unlikeLoading" @click="handleClickArticleUnlike">不喜欢</van-button>
            </van-col>
        </van-row>

        <!-- 文章评论列表 -->
        <van-list v-model="commentsLoading" :finished="commentsFinished" finished-text="没有更多了"
                  @load="onLoadArticleComments">
            <van-row id="#comment-view" type="flex" class="article-comment" v-for="(item, index) in comments" :key="index">
                <van-col span="3">
                    <van-image round src="https://p3.pstatp.com/thumb/1480/7186611868" class="article-avatar"></van-image>
                </van-col>
                <van-col span="21">
                    <van-row type="flex" align="center" justify="space-between">
                        <van-col class="comment-author" v-html="item.authorName"></van-col>
                        <van-col>
                            <van-button round :icon="item.operation === 0 ? 'good-job' : 'good-job-o'" size="normal"
                                        @click="handleClickCommentLike(item)">{{ item.likes || '' }}
                            </van-button>
                        </van-col>
                    </van-row>

                    <van-row>
                        <van-col class="comment-content" v-html="item.content"></van-col>
                    </van-row>
                    <van-row type="flex" align="center">
                        <van-col span="10" class="comment-time">
                            {{ item.createdTime | timestampToDateTime }}
                        </van-col>
                        <van-col span="3">
                            <van-button round size="normal" v-html="item.reply" @click="showCommentRepliesPopup(item.id)">回复 {{
                                item.reply || '' }}
                            </van-button>
                        </van-col>
                    </van-row>
                </van-col>
            </van-row>
        </van-list>
    </div>
    <!-- 文章底部栏 -->
    <van-row type="flex" justify="space-around" align="center" class="article-bottom-bar">
        <van-col span="13">
            <van-field v-model="commentValue" placeholder="写评论">
                <template #button>
                    <van-button icon="back-top" @click="handleSaveComment"></van-button>
                </template>
            </van-field>
        </van-col>
        <van-col span="3">
            <van-button icon="comment-o" @click="handleScrollIntoCommentView"></van-button>
        </van-col>
        <van-col span="3">
            <van-button :icon="relation.iscollection ? 'star' : 'star-o'" :loading="collectionLoading"
                        @click="handleClickArticleCollection"></van-button>
        </van-col>
        <van-col span="3">
            <van-button icon="share-o"></van-button>
        </van-col>
    </van-row>

    <!-- 评论Popup 弹出层 -->
    <van-popup v-model="showPopup" closeable position="bottom"
               :style="{ width: '750px', height: '60%', left: '50%', 'margin-left': '-375px' }">
        <!-- 评论回复列表 -->
        <van-list v-model="commentRepliesLoading" :finished="commentRepliesFinished" finished-text="没有更多了"
                  @load="onLoadCommentReplies">
            <van-row id="#comment-reply-view" type="flex" class="article-comment-reply"
                     v-for="(item, index) in commentReplies" :key="index">
                <van-col span="3">
                    <van-image round src="https://p3.pstatp.com/thumb/1480/7186611868" class="article-avatar"></van-image>
                </van-col>
                <van-col span="21">
                    <van-row type="flex" align="center" justify="space-between">
                        <van-col class="comment-author" v-html="item.authorName"></van-col>
                        <van-col>
                            <van-button round :icon="item.operation === 0 ? 'good-job' : 'good-job-o'" size="normal"
                                        @click="handleClickCommentReplyLike(item)">{{ item.likes || '' }}
                            </van-button>
                        </van-col>
                    </van-row>

                    <van-row>
                        <van-col class="comment-content" v-html="item.content"></van-col>
                    </van-row>
                    <van-row type="flex" align="center">
                        <!-- TODO: js计算时间差 -->
                        <van-col span="10" class="comment-time">
                            {{ item.createdTime | timestampToDateTime }}
                        </van-col>
                    </van-row>
                </van-col>
            </van-row>
        </van-list>
        <!-- 评论回复底部栏 -->
        <van-row type="flex" justify="space-around" align="center" class="comment-reply-bottom-bar">
            <van-col span="13">
                <van-field v-model="commentReplyValue" placeholder="写评论">
                    <template #button>
                        <van-button icon="back-top" @click="handleSaveCommentReply"></van-button>
                    </template>
                </van-field>
            </van-col>
            <van-col span="3">
                <van-button icon="comment-o"></van-button>
            </van-col>
            <van-col span="3">
                <van-button icon="star-o"></van-button>
            </van-col>
            <van-col span="3">
                <van-button icon="share-o"></van-button>
            </van-col>
        </van-row>
    </van-popup>
</div>

<!-- 引入 Vue 和 Vant 的 JS 文件 -->
<script src=" https://cdn.jsdelivr.net/npm/vue/dist/vue.min.js">
</script>
<script src="https://cdn.jsdelivr.net/npm/vant@2.12.20/lib/vant.min.js"></script>
<!-- 引入 Axios 的 JS 文件 -->
<#--<script src="https://unpkg.com/axios/dist/axios.min.js"></script>-->
<script src="../../../plugins/js/axios.min.js"></script>
<!-- 页面逻辑 -->
<script src="../../../plugins/js/index.js"></script>
</body>

</html>
```

### 上传 css/js 文件

模板文件得到内容会生成 html 文件并展示，因此需要 css、JS 文件装饰，在 minio 上传所需文件 `plugins\css\index.css`、`plugins\js\axios.min.js`、`plugins\js\index.js`

```xml
<dependencies>
    <!-- 创建的minio starter里面并没有定义css/js的上传方法，原生上传 -->
    <!-- 导入minio依赖 -->
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>7.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
</dependencies>
```

```java
package com.linxuan.minio;

@SpringBootApplication
public class MinIOApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinIOApplication.class, args);
    }
}
```

```java
package com.linxuan.minio.test;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinIOApplication.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void testMinIO() {
        Map<String, String> map = new HashMap<>();
        map.put("D:\\plugins\\css\\index.css", "plugins/css/index.css");
        map.put("D:\\plugins\\js\\index.js", "plugins/js/index.js");
        map.put("D:\\plugins\\js\\axios.min.js", "plugins/js/axios.min.js");

        map.forEach((key, value) -> {
            // 获取文件后缀类型
            int lastIndexOf = value.lastIndexOf(".");
            String suffix = null;
            if (lastIndexOf != -1) {
                suffix = value.substring(lastIndexOf + 1);
            } else {
                suffix = "css";
            }

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(key);

                // 创建minio客户端
                MinioClient minioClient = MinioClient.builder()
                        .credentials("minio", "minio123")
                        .endpoint("http://192.168.88.129:9000")
                        .build();
                // 上传文件
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .object(value) // 创建文件名称
                        .contentType("text/" + suffix) // 文件类型
                        .bucket("leadnews") // 桶名词，和minio中创建的一致
                        .stream(fileInputStream, fileInputStream.available(), -1)
                        .build();
                minioClient.putObject(putObjectArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
```

### 测试生成详情页

```java
package com.linxuan.article;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.linxuan.article.mapper")
public class ArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class,args);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

```java
package com.linxuan.article.mapper;

public interface ApArticleContentMapper extends BaseMapper<ApArticleContent> {
}
```

```java
package com.linxuan.article.test;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class ArticleFreemarkerTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Test
    public void createStaticUrlTest() throws Exception {
        // 获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper
                .selectOne(new LambdaQueryWrapper<ApArticleContent>()
                        .eq(ApArticleContent::getArticleId, "1383828014629179393"));
        
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            // 通过freemarker生成html文件
            Template template = configuration.getTemplate("article.ftl");
            // 创建数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            StringWriter out = new StringWriter();
            // 合成html文件
            template.process(map, out);

            // 上传html文件到minio中
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String filePath = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

            // 修改ap_article表，保存static_url字段
            apArticleService.update(new LambdaUpdateWrapper<ApArticle>()
                    .eq(ApArticle::getId, apArticleContent.getArticleId())
                    .set(ApArticle::getStaticUrl, filePath));
        }
    }
}
```

### 详情展示

该功能由前端实现了，点击文章列表，会访问 ap_article表保存的 static_url 字段
