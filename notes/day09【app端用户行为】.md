## 用户行为-需求

### 1 什么是行为

用户行为数据的记录包括了关注、点赞、不喜欢、收藏、阅读等行为

黑马头条项目整个项目开发涉及web展示和大数据分析来给用户推荐文章，如何找出哪些文章是热点文章进行针对性的推荐呢？这个时候需要进行大数据分析的准备工作，埋点。

所谓“埋点”，是数据采集领域（尤其是用户行为数据采集领域）的术语，指的是针对特定用户行为或事件进行捕获、处理和发送的相关技术及其实施过程。比如用户某个icon点击次数、阅读文章的时长，观看视频的时长等等。

### 2 关注

![image-20210727162600274](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727162600274.png)

如上效果：

当前登录后的用户可以关注作者，也可以取消关注作者



一个用户关注了作者，作者是由用户实名认证以后开通的作者权限，才有了作者信息，作者肯定是app中的一个用户。

从用户的角度出发：一个用户可以关注其他多个作者 —— 我的关注

从作者的角度出发：一个用户（同是作者）也可以拥有很多个粉丝 —— 我的粉丝

![image-20210727163038634](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727163038634.png)

### 3 点赞或取消点赞

![image-20210727161838807](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727161838807.png)

- 当前登录的用户点击了”赞“,就要保存当前行为数据
- 可以取消点赞

### 4 阅读

当用户查看了某一篇文章，需要记录当前用户查看的次数，阅读时长（非必要），阅读文章的比例（非必要），加载的时长（非必要）

### 5 不喜欢

为什么会有不喜欢？

一旦用户点击了不喜欢，不再给当前用户推荐这一类型的文章信息

![image-20210727162221437](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727162221437.png)



### 6 收藏

![image-20210727162332931](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727162332931.png)

记录当前登录人收藏的文章

### 7 文章详情-行为数据回显

主要是用来展示文章的关系，app端用户必须登录，判断当前用户**是否已经关注该文章的作者、是否收藏了此文章、是否点赞了文章、是否不喜欢该文章等**

例：如果当前用户点赞了该文章，点赞按钮进行高亮，其他功能类似。

![](D:\Java\IdeaProjects\lead_news\!information\day09\需求说明\用户行为-需求.assets\image-20210727162449406.png)

### 8 注意：

- 所有的行为数据，都存储到redis中
- 点赞、阅读、不喜欢需要专门创建一个微服务来处理数据，新建模块：linxuan-leadnews-behavior
- 关注需要在linxuan-leadnews-user微服务中实现
- 收藏与文章详情数据回显在linxuan-leadnews-article微服务中实现



## 用户行为-接口文档

### 1 点赞

**接口地址**:`/api/v1/likes_behavior`


**请求方式**:`POST`

**请求数据类型**:`application/json`

**响应数据类型**:`*/*`

**接口描述**:


**请求示例**:


```javascript
{
	"articleId": 0,
	"operation": 0,
	"type": 0
}
```


**请求参数**:


| 参数名称              | 参数说明             | in   | 是否必须 | 数据类型         | schema           |
| --------------------- | -------------------- | ---- | -------- | ---------------- | ---------------- |
| dto                   | dto                  | body | true     | LikesBehaviorDto | LikesBehaviorDto |
| &emsp;&emsp;articleId | 文章id               |      | false    | long             |                  |
| &emsp;&emsp;operation | 0 点赞   1 取消点赞  |      | false    | short            |                  |
| &emsp;&emsp;type      | 0文章  1动态   2评论 |      | false    | short            |                  |

**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {},
	"errorMessage": "",
	"host": ""
}
```




### 2 阅读


**接口地址**:`/api/v1/read_behavior`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
	"articleId": 0,
	"count": 0
}
```

**请求参数**:


| 参数名称              | 参数说明 | in   | 是否必须 |
| --------------------- | -------- | ---- | -------- |
| dto                   | dto      | body | true     |
| &emsp;&emsp;articleId | 文章id   | long | false    |
| &emsp;&emsp;count     | 阅读次数 | int  | false    |


**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {},
	"errorMessage": "",
	"host": ""
}
```


### 3 不喜欢


**接口地址**:`/api/v1/un_likes_behavior`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`

**接口描述**:

**请求示例**:


```javascript
{
	"articleId": 0,
	"type": 0
}
```

**请求参数**:


| 参数名称              | 参数说明                   | in   | 是否必须 | 数据类型           | schema             |
| --------------------- | -------------------------- | ---- | -------- | ------------------ | ------------------ |
| dto                   | dto                        | body | true     | UnLikesBehaviorDto | UnLikesBehaviorDto |
| &emsp;&emsp;articleId | 文章id                     |      | false    | long               |                    |
| &emsp;&emsp;type      | 0 不喜欢      1 取消不喜欢 |      | false    | short              |                    |

**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {},
	"errorMessage": "",
	"host": ""
}
```

### 4 关注与取消关注

**接口地址**:`/api/v1/user/user_follow`

**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
	"articleId": 0,
	"authorId": 0,
	"operation": 0
}
```


**请求参数**:


| 参数名称              | 参数说明          | in   | 是否必须 | 数据类型        | schema          |
| --------------------- | ----------------- | ---- | -------- | --------------- | --------------- |
| dto                   | dto               | body | true     | UserRelationDto | UserRelationDto |
| &emsp;&emsp;articleId | 文章id            |      | false    | long            |                 |
| &emsp;&emsp;authorId  | 作者id            |      | false    | int             |                 |
| &emsp;&emsp;operation | 0  关注   1  取消 |      | false    | short           |                 |


**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {},
	"errorMessage": "",
	"host": ""
}
```

### 5 文章收藏

**接口地址**:`/api/v1/collection_behavior`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
	"entryId": 0,
	"operation": 0,
	"publishedTime": "",
	"type": 0
}
```


**请求参数**:


| 参数名称                  | 参数说明           | in   | 是否必须 | 数据类型              | schema                |
| ------------------------- | ------------------ | ---- | -------- | --------------------- | --------------------- |
| dto                       | dto                | body | true     | CollectionBehaviorDto | CollectionBehaviorDto |
| &emsp;&emsp;entryId       | 文章id             |      | false    | long                  |                       |
| &emsp;&emsp;operation     | 0收藏    1取消收藏 |      | false    | short                 |                       |
| &emsp;&emsp;publishedTime | 发布时间           |      | false    | date                  |                       |
| &emsp;&emsp;type          | 0文章    1动态     |      | false    | short                 |                       |


**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {},
	"errorMessage": "",
	"host": ""
}
```

### 6 加载文章行为-数据回显

**接口地址**:`/api/v1/article/load_article_behavior`

**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
	"articleId": 0,
	"authorId": 0
}
```


**请求参数**:


| 参数名称              | 参数说明 | in   | 是否必须 | 数据类型       | schema         |
| --------------------- | -------- | ---- | -------- | -------------- | -------------- |
| dto                   | dto      | body | true     | ArticleInfoDto | ArticleInfoDto |
| &emsp;&emsp;articleId | 文章id   |      | false    | long           |                |
| &emsp;&emsp;authorId  | 作者id   |      | false    | int            |                |


**响应状态**:


| 状态码 | 说明         | schema         |
| ------ | ------------ | -------------- |
| 200    | OK           | ResponseResult |
| 201    | Created      |                |
| 401    | Unauthorized |                |
| 403    | Forbidden    |                |
| 404    | Not Found    |                |


**响应参数**:


| 参数名称     | 参数说明 | 类型           | schema         |
| ------------ | -------- | -------------- | -------------- |
| code         |          | integer(int32) | integer(int32) |
| data         |          | object         |                |
| errorMessage |          | string         |                |
| host         |          | string         |                |


**响应示例**:

```javascript
{
    "host":null,
    "code":200,
    "errorMessage":"操作成功",
    "data":{
        "islike":false,
        "isunlike":false,
        "iscollection":false,
        "isfollow":false
    }
}
```

