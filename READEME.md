1.前端用户功能模块
1）用户端注册与登录：用户可以在APP端注册后登录到系统中。
2）用户端文章列表：用户进入后会根据个性化推荐算法来显示文章列表。
3）用户端文章详情：用户点击文章之后可以进入到文章详情页面，该页面主要有文章内容、评论、喜欢、在看等区域。
4）用户端搜索文章：该功能模块供用户搜索文章，采用ES进行搜索，并且添加了历史记录功能，将历史记录存储于数据库方便用户下次进行搜索。
5）热点推荐：通过定时计算热文章并将其分数缓存，可以进行热点推荐。

2.自媒体端用户功能模块
1）自媒体端用户登录功能：用户在网页端登录之后才可以访问平台。
2）自媒体端编辑文章功能：自媒体用户登录系统后台可以进行编辑文章，然后将其发表至头条平台供用户浏览。
3）自媒体端素材上传功能：如果自媒体用户有额外的素材需要上传，那么可以将其上传至系统并存储在数据库中。
4）自媒体端用户发布文章：自媒体用户编辑号文章之后可以将其发布。
5）自媒体端审核文章功能：自媒体用户发布文章之后系统会自动进行审核，如果发现有违规的地方那么无法发布。
6）自媒体端用户搜索功能：自媒体用户发布文章之后可以根据文章关键字、频道列表、发布日期等进行搜索。
7）自媒体端文章延时发布：自媒体用户可以定时发布文章。