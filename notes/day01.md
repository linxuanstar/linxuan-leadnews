## 技术栈

- Spring-Cloud-Gateway : 微服务之前架设的网关服务，实现服务注册中的API请求路由，以及控制流速控制和熔断处理都是常用的架构手段，而这些功能Gateway天然支持
- 运用Spring Boot快速开发框架，构建项目工程；并结合Spring Cloud全家桶技术，实现后端个人中心、自媒体、管理中心等微服务。
- 运用Spring Cloud Alibaba Nacos作为项目中的注册中心和配置中心
- 运用mybatis-plus作为持久层提升开发效率
- 运用Kafka完成内部系统消息通知；与客户端系统消息通知；以及实时数据计算
- 运用Redis缓存技术，实现热数据的计算，提升系统性能指标
- 使用Mysql存储用户数据，以保证上层数据查询的高性能
- 使用Mongo存储用户热数据，以保证用户热数据高扩展和高性能指标
- 使用FastDFS作为静态资源存储器，在其上实现热静态资源缓存、淘汰等功能
- 运用Hbase技术，存储系统中的冷数据，保证系统数据的可靠性
- 运用ES搜索技术，对冷数据、文章数据建立索引，以保证冷数据、文章查询性能
- 运用AI技术，来完成系统自动化功能，以提升效率及节省成本。比如实名认证自动化
- PMD&P3C : 静态代码扫描工具，在项目中扫描项目代码，检查异常点、优化点、代码规范等，为开发团队提供规范统一，提升项目代码质量

## 初始项目

[项目资料](https://pan.baidu.com/s/1u7PNExeoEsYGh5pAnSyocg?pwd=9987)

### 安装 Centos7 虚拟机

### 配置相应 IP

VM Workstation 中 VMnet8 的子网 IP 设置为 192.168.88.0、子网掩码设置为 255.255.255.0、网关 IP 设置为192.168.888.2。

Win10 的「查看网络连接」中 VMware Network Adapter VMnet8 的 「Internet 协议版本4（TCP/IPv4）」的IP地质设置为 192.168.88.1、子网掩码设置为 255.255.255.0、网关 IP 设置为192.168.888.2、首选 DNS 服务器设置为192.168.88.2、备用 DNS 服务器设置为114.114.114.114。

打开 Centos7 虚拟机配置 IP

```bash
# 修改配置文件将最后一行属性的值改为yes
[root@localhost ~]# vi /etc/sysconfig/network-scripts/ifcfg-ens33
ONBOOT=yes
# 重启网络服务
[root@localhost ~]# service network restart 
[root@localhost ~]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 00:0c:29:cf:55:76 brd ff:ff:ff:ff:ff:ff
    # 192.168.88.129即是该虚拟机IP地址
    inet 192.168.88.129/24 brd 192.168.88.255 scope global noprefixroute ens33
       valid_lft forever preferred_lft forever
    inet6 fe80::17de:155:4eae:6665/64 scope link noprefixroute 
       valid_lft forever preferred_lft forever
# 设置静态IP，将该IP地址192.168.88.129固定下来
[root@localhost ~]# vi /etc/sysconfig/network-scripts/ifcfg-ens33
# 将BOOTPROTO的属性值改为static
BOOTPROTO=static
# 添加IP地址、默认网关、子网掩码、谷歌域名服务器。该四行放到最后即可
IPADDR=192.168.88.129
GATEWAY=192.168.88.2
NETMASK=255.255.255.0
DNS1=8.8.8.8
# 重启网络服务
[root@localhost ~]# service network restart 
# 测试是否能够连通网络
[root@localhost ~]# ping www.baidu.com
PING www.a.shifen.com (39.156.66.14) 56(84) bytes of data.
64 bytes from 39.156.66.14 (39.156.66.14): icmp_seq=1 ttl=128 time=40.0 ms
```

### 下载 Docker

```bash
# 卸载Docker
[root@node1 ~]# yum remove docker \
                    docker-client \
                    docker-client-latest \
                    docker-common \
                    docker-latest \
                    docker-latest-logrotate \
                    docker-logrotate \
                    docker-selinux \
                    docker-engine-selinux \
                    docker-engine \
                    docker-ce
# 虚拟机联网，安装yum工具
[root@node1 ~]# yum install -y yum-utils \
                    device-mapper-persistent-data \
                    lvm2 --skip-broken
# 设置docker镜像源
[root@node1 ~]# yum-config-manager \
                    --add-repo \
                    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
                    
# sed以流的方式编辑文本 -i直接修改读取的文件内容 s取代，搭配正则表达式进行取代 g全部替换
# 替换/etc/yum.repos.d/docker-ce.repo文件中download.docker.com为mirrors.aliyun.com/docker-ce
[root@node1 ~]# sed -i 's/download.docker.com/mirrors.aliyun.com\/docker-ce/g' /etc/yum.repos.d/docker-ce.repo

# 加速yum的更新速度
[root@node1 ~]# yum makecache fast

# yum方式安装docker-ce
[root@node1 ~]# yum install -y docker-ce
```

docker-ce 为社区免费版本。稍等片刻，docker 即可安装成功。安装成功之后关闭防火墙，Docker应用需要用到各种端口，逐一去修改防火墙设置非常麻烦。基本命令如下：

| 命令                        | 作用               |
| --------------------------- | ------------------ |
| systemctl stop firewalld    | 关闭防火墙         |
| systemctl disable firewalld | 禁止开机启动防火墙 |
| systemctl start docker      | 启动docker服务     |
| systemctl stop docker       | 停止docker服务     |
| systemctl restart docker    | 重启docker服务     |
| docker -v                   | 查看docker版本     |

```sh
# docker在关闭状态下被访问自动唤醒机制，很人性化，即这时再执行任意docker命令会直接启动。
[root@node1 ~]# systemctl stop docker
Warning: Stopping docker.service, but it can still be activated by:
  docker.socket
```

docker官方镜像仓库网速较差，我们需要设置国内镜像服务。参考阿里云的镜像加速文档：https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors，内容如下，将其复制到shell窗口即可：

```sh
sudo mkdir -p /etc/docker

sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://zfz45z36.mirror.aliyuncs.com"]
}
EOF

sudo systemctl daemon-reload
sudo systemctl restart docker
```

#### 安装 Nacos

```shell
# docker拉取nacos镜像
[root@localhost ~]# docker pull nacos/nacos-server:1.2.0
1.2.0: Pulling from nacos/nacos-server
# 创建容器
# --env MODE=standalone 设置环境变量Nacos单机模式运行、--name nacos容器命名为nacos
# --restart=always开机启动、-d后台运行、-p 8848:8848映射端口号、nacos/nacos-server:1.2.0使用的镜像
[root@localhost ~]# docker run --env MODE=standalone --name nacos --restart=always -d -p 8848:8848 nacos/nacos-server:1.2.0
```

本地访问 [Nacos 地址 ](http://192.168.88.129:8848/nacos/)即可！

```sh
# 查看所有镜像
[root@localhost ~]# docker ps
CONTAINER ID   IMAGE
91867ec6b367   nacos/nacos-server:1.2.0
# 查看nacos日志
[root@localhost ~]# docker logs -f 91867ec6b367
```

### 导入项目

```bash
linxuan-leadnews
  |
  |-- leadnews-common    # 通用模块
  |-- leadnews-fign-api  # 远程接口调用
  |-- leadnews-gateway   # 网关管理
  |-- leadnews-model     # 实体类
  |-- leadnews-service   # 微服务工程模块类，对微服务工程做统一管理
  |-- leadnews-test      # 测试模块
  |-- leadnews-utils     # 工具模块
  |-- linxuan-leadnews.iml
  |-- pom.xml
```

## 通用类开发

### 响应状态码

```java
package com.linxuan.model.common.enums;

public enum AppHttpCodeEnum {

    // 成功段0
    SUCCESS(200,"操作成功"),
    // 登录段1~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2,"密码错误"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过期"),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    SERVER_ERROR(503,"服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    AP_USER_DATA_NOT_EXIST(1001,"ApUser数据不存在"),
    DATA_NOT_EXIST(1002,"数据不存在"),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操作"),
    NEED_ADMIND(3001,"需要管理员权限");

    int code;
    String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
```

### 返回结果类

```java
package com.linxuan.model.common.dtos;

/**
 * 通用的结果返回类
 * @param <T>
 */
public class ResponseResult<T> implements Serializable {

    private String host;

    private Integer code;

    private String errorMessage;

    private T data;

    public ResponseResult() {
        this.code = 200;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.errorMessage = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static ResponseResult errorResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.error(code, msg);
    }

    public static ResponseResult okResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }

    public static ResponseResult okResult(Object data) {
        ResponseResult result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getErrorMessage());
        if(data!=null) {
            result.setData(data);
        }
        return result;
    }

    public static ResponseResult errorResult(AppHttpCodeEnum enums){
        return setAppHttpCodeEnum(enums,enums.getErrorMessage());
    }

    public static ResponseResult errorResult(AppHttpCodeEnum enums, String errorMessage){
        return setAppHttpCodeEnum(enums,errorMessage);
    }

    public static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums){
        return okResult(enums.getCode(),enums.getErrorMessage());
    }

    private static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums, String errorMessage){
        return okResult(enums.getCode(),errorMessage);
    }

    public ResponseResult<?> error(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.errorMessage = msg;
        return this;
    }

    public ResponseResult<?> ok(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
```

## 用户功能开发

### 导入数据库及表

| leadnews_user库中表名称 | 用户库说明        |
| ----------------------- | ----------------- |
| ap_user                 | APP用户信息表     |
| ap_user_fan             | APP用户粉丝信息表 |
| ap_user_follow          | APP用户关注信息表 |
| ap_user_realname        | APP实名认证信息表 |

```sql
CREATE DATABASE IF NOT EXISTS leadnews_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadnews_user;
SET NAMES utf8;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ap_user
-- ----------------------------
DROP TABLE IF EXISTS `ap_user`;
CREATE TABLE `ap_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `salt` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码、通信等加密盐',
  `name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码,md5加密',
  `phone` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `sex` tinyint(1) unsigned DEFAULT NULL COMMENT '0 男\r\n            1 女\r\n            2 未知',
  `is_certification` tinyint(1) unsigned DEFAULT NULL COMMENT '0 未\r\n            1 是',
  `is_identity_authentication` tinyint(1) DEFAULT NULL COMMENT '是否身份认证',
  `status` tinyint(1) unsigned DEFAULT NULL COMMENT '0正常\r\n            1锁定',
  `flag` tinyint(1) unsigned DEFAULT NULL COMMENT '0 普通用户\r\n            1 自媒体人\r\n            2 大V',
  `created_time` datetime DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP用户信息表';

-- ----------------------------
-- Records of ap_user
-- ----------------------------
INSERT INTO `ap_user` VALUES ('1', 'abc', 'zhangsan', 'abc', '13511223453', null, '1', null, null, '1', '1', '2020-03-19 23:22:07');
INSERT INTO `ap_user` VALUES ('2', 'abc', 'lisi', 'abc', '13511223454', '', '1', null, null, '1', '1', '2020-03-19 23:22:07');
INSERT INTO `ap_user` VALUES ('3', 'sdsa', 'wangwu', 'wangwu', '13511223455', null, null, null, null, null, '1', null);
INSERT INTO `ap_user` VALUES ('4', '123abc', 'admin', '81e158e10201b6d7aee6e35eaf744796', '13511223456', null, '1', null, null, '1', '1', '2020-03-30 16:36:32');
INSERT INTO `ap_user` VALUES ('5', '123', 'suwukong', 'suwukong', '13511223458', null, '1', null, null, '1', '1', '2020-08-01 11:09:57');
INSERT INTO `ap_user` VALUES ('6', null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for ap_user_fan
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_fan`;
CREATE TABLE `ap_user_fan` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
  `fans_id` int(11) unsigned DEFAULT NULL COMMENT '粉丝ID',
  `fans_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '粉丝昵称',
  `level` tinyint(1) unsigned DEFAULT NULL COMMENT '粉丝忠实度\r\n            0 正常\r\n            1 潜力股\r\n            2 勇士\r\n            3 铁杆\r\n            4 老铁',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `is_display` tinyint(1) unsigned DEFAULT NULL COMMENT '是否可见我动态',
  `is_shield_letter` tinyint(1) unsigned DEFAULT NULL COMMENT '是否屏蔽私信',
  `is_shield_comment` tinyint(1) unsigned DEFAULT NULL COMMENT '是否屏蔽评论',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP用户粉丝信息表';

-- ----------------------------
-- Table structure for ap_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_follow`;
CREATE TABLE `ap_user_follow` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
  `follow_id` int(11) unsigned DEFAULT NULL COMMENT '关注作者ID',
  `follow_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '粉丝昵称',
  `level` tinyint(1) unsigned DEFAULT NULL COMMENT '关注度\r\n            0 偶尔感兴趣\r\n            1 一般\r\n            2 经常\r\n            3 高度',
  `is_notice` tinyint(1) unsigned DEFAULT NULL COMMENT '是否动态通知',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP用户关注信息表';

-- ----------------------------
-- Table structure for ap_user_realname
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_realname`;
CREATE TABLE `ap_user_realname` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '账号ID',
  `name` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '用户名称',
  `idno` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源名称',
  `font_image` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '正面照片',
  `back_image` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背面照片',
  `hold_image` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手持照片',
  `live_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活体照片',
  `status` tinyint(1) unsigned DEFAULT NULL COMMENT '状态\r\n            0 创建中\r\n            1 待审核\r\n            2 审核失败\r\n            9 审核通过',
  `reason` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝原因',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `submited_time` datetime DEFAULT NULL COMMENT '提交时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP实名认证信息表';

-- ----------------------------
-- Records of ap_user_realname
-- ----------------------------
INSERT INTO `ap_user_realname` VALUES ('1', '1', 'zhangsan', '512335455602781278', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', '9', '', '2019-07-30 14:34:28', '2019-07-30 14:34:30', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES ('2', '2', 'lisi', '512335455602781279', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', '1', '', '2019-07-11 17:21:18', '2019-07-11 17:21:20', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES ('3', '3', 'wangwu6666', '512335455602781276', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', '9', '', '2019-07-11 17:21:18', '2019-07-11 17:21:20', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES ('5', '5', 'suwukong', '512335455602781279', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', '1', '', '2020-08-01 11:10:31', '2020-08-01 11:10:34', '2020-08-01 11:10:36');
```

| leadnews_article库中表名称 | 文章库说明                   |
| -------------------------- | ---------------------------- |
| ap_article                 | 文章信息表，存储已发布的文章 |
| ap_article_config          | APP 已发布文章配置表         |
| ap_article_content         | APP 已发布文章内容表         |
| ap_author                  | APP 文章作者信息表           |
| ap_collection              | APP 收藏信息表               |

```sql
CREATE DATABASE IF NOT EXISTS leadnews_article DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadnews_article;
SET NAMES utf8;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ap_article
-- ----------------------------
DROP TABLE IF EXISTS `ap_article`;
CREATE TABLE `ap_article` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `author_id` int(11) unsigned DEFAULT NULL COMMENT '文章作者的ID',
  `author_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者昵称',
  `channel_id` int(10) unsigned DEFAULT NULL COMMENT '文章所属频道ID',
  `channel_name` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频道名称',
  `layout` tinyint(1) unsigned DEFAULT NULL COMMENT '文章布局\r\n            0 无图文章\r\n            1 单图文章\r\n            2 多图文章',
  `flag` tinyint(3) unsigned DEFAULT NULL COMMENT '文章标记\r\n            0 普通文章\r\n            1 热点文章\r\n            2 置顶文章\r\n            3 精品文章\r\n            4 大V 文章',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章图片\r\n            多张逗号分隔',
  `labels` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章标签最多3个 逗号分隔',
  `likes` int(5) unsigned DEFAULT NULL COMMENT '点赞数量',
  `collection` int(5) unsigned DEFAULT NULL COMMENT '收藏数量',
  `comment` int(5) unsigned DEFAULT NULL COMMENT '评论数量',
  `views` int(5) unsigned DEFAULT NULL COMMENT '阅读数量',
  `province_id` int(11) unsigned DEFAULT NULL COMMENT '省市',
  `city_id` int(11) unsigned DEFAULT NULL COMMENT '市区',
  `county_id` int(11) unsigned DEFAULT NULL COMMENT '区县',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `sync_status` tinyint(1) DEFAULT '0' COMMENT '同步状态',
  `origin` tinyint(1) unsigned DEFAULT '0' COMMENT '来源',
  `static_url` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1383828014629179394 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文章信息表，存储已发布的文章';

-- ----------------------------
-- Records of ap_article
-- ----------------------------
INSERT INTO `ap_article` VALUES ('1302862387124125698', '什么是Java语言', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl9V2CqAZe18AAOoOOsvWPc041.png', null, null, null, null, null, null, null, null, '2020-09-07 14:52:54', '2020-09-07 14:56:18', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302864436297482242', 'Java语言跨平台原理', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl9V2n6AArZsAAGMmaPdt7w502.png', null, null, null, null, null, null, null, null, '2020-09-07 15:01:02', '2020-09-07 15:01:02', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302864730402078722', '我是一个测试标题', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg', null, null, null, null, null, null, null, null, '2020-09-07 15:02:12', '2020-09-07 15:02:12', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302865008438296577', '过山车故障20名游客倒挂空中', '4', 'admin', '1', 'java', '3', null, 'group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg,group1/M00/00/00/wKjIgl892xmAG_yjAAB6OkkuJd4819.jpg,group1/M00/00/00/wKjIgl892wKAZLhtAASZUi49De0836.jpg', null, null, null, null, null, null, null, null, '2020-09-07 15:03:19', '2020-09-07 15:03:19', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302865306489733122', '武汉高校开学典礼万人歌唱祖国', '4', 'admin', '1', 'java', '3', null, 'group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg,group1/M00/00/00/wKjIgl892xGANV6qAABzWOH8KDY775.jpg,group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg', null, null, null, null, null, null, null, null, '2020-09-07 15:04:30', '2020-09-07 15:04:30', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302865474094120961', '天降铁球砸死女婴整栋楼被判赔', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl892tyAFc60AAMUNUuOKPA619.jpg', null, null, null, null, null, null, null, null, '2020-09-07 15:05:10', '2020-09-07 15:05:10', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977178887004162', '央视曝光境外医疗豪华旅游套路', '4', 'admin', '1', 'java', '0', null, 'group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg', null, null, null, null, null, null, null, null, '2020-09-07 22:29:02', '2020-09-07 22:29:02', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977458215067649', '10多名陌生人合力托举悬窗女童', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl892vOASiunAAGzs3UZ1Cg252.jpg', null, null, null, null, null, null, null, null, '2020-09-07 22:30:09', '2020-09-07 22:30:09', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977558807060482', '杨澜回应一秒变脸', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl892wKAZLhtAASZUi49De0836.jpg', null, null, null, null, null, null, null, null, '2020-09-07 22:30:33', '2020-09-07 22:30:33', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977754114826241', '黄龄工作室发视频回应', '4', 'admin', '4', 'Python', '1', null, 'group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg', null, null, null, null, null, null, null, null, '2020-09-07 22:31:19', '2020-09-07 22:31:19', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977754114826242', '黄龄工作室发视频回应', '4', 'admin', '4', 'Python', '1', null, 'group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg', '', null, null, null, null, null, null, null, '2020-09-07 22:31:19', '2020-09-07 22:31:19', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1302977754114826243', '黄龄工作室发视频回应', '4', 'admin', '4', 'Python', '1', null, 'group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg', '', null, null, null, null, null, null, null, '2020-09-07 22:31:19', '2020-09-07 22:31:19', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1303156149041758210', '全国抗击新冠肺炎疫情表彰大会', '4', 'admin', '1', 'java', '1', null, 'group1/M00/00/00/wKjIgl9W6iOAD2doAAFY4E1K7-g384.png', null, null, null, null, null, null, null, null, '2020-09-08 10:20:12', '2020-09-08 10:20:12', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827787629252610', 'Kafka文件的存储机制', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:00:29', '2021-04-19 00:20:17', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827888816836609', '为什么项目经理不喜欢重构？', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:00:54', '2021-04-19 00:19:09', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827911810011137', 'Base64编解码原理', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/b44c65376f12498e873223d9d6fdf523.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:00:59', '2021-04-19 00:17:42', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827952326987778', '工作线程数是不是设置的越大越好', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/a3f0bc438c244f788f2df474ed8ecdc1.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:01:09', '2021-04-19 00:16:52', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827976310018049', '小白如何辨别其真与伪&好与坏？', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/1818283261e3401892e1383c1bd00596.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:01:14', '2021-04-19 00:14:58', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383827995813531650', '学IT，为什么要学项目课程？', '4', 'admin', '1', 'java', '3', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg,http://192.168.200.130:9000/leadnews/2021/4/20210418/c7c3d36d25504cf6aecdcd5710261773.jpg,http://192.168.200.130:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:01:19', '2021-04-19 00:10:48', '0', '0', null);
INSERT INTO `ap_article` VALUES ('1383828014629179393', '“真”项目课程对找工作有什么帮助？', '4', 'admin', '1', 'java', '1', null, 'http://192.168.200.130:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg', null, null, null, null, null, null, null, null, '2021-04-19 01:01:24', '2021-04-19 00:08:05', '0', '0', null);

-- ----------------------------
-- Table structure for ap_article_config
-- ----------------------------
DROP TABLE IF EXISTS `ap_article_config`;
CREATE TABLE `ap_article_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `article_id` bigint(20) unsigned DEFAULT NULL COMMENT '文章ID',
  `is_comment` tinyint(1) unsigned DEFAULT NULL COMMENT '是否可评论',
  `is_forward` tinyint(1) unsigned DEFAULT NULL COMMENT '是否转发',
  `is_down` tinyint(1) unsigned DEFAULT NULL COMMENT '是否下架',
  `is_delete` tinyint(1) unsigned DEFAULT NULL COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1383828014645956610 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP已发布文章配置表';

-- ----------------------------
-- Records of ap_article_config
-- ----------------------------
INSERT INTO `ap_article_config` VALUES ('1302862387933626369', '1302862387124125698', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302864437425750018', '1302864436297482242', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302864731203190785', '1302864730402078722', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302865009533009922', '1302865008438296577', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302865307408285697', '1302865306489733122', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302865475297886209', '1302865474094120961', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977180199821313', '1302977178887004162', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977459322363905', '1302977458215067649', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977559788527618', '1302977558807060482', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977754882383873', '1302977754114826241', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977754882383874', '1302977754114826242', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1302977754882383875', '1302977754114826243', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1303156149909979137', '1303156149041758210', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827787704750082', '1383827787629252610', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827888829419522', '1383827888816836609', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827911822594049', '1383827911810011137', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827952326987779', '1383827952326987778', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827976322600962', '1383827976310018049', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383827995821920257', '1383827995813531650', '1', '1', '0', '0');
INSERT INTO `ap_article_config` VALUES ('1383828014645956609', '1383828014629179393', '1', '1', '0', '0');

-- ----------------------------
-- Table structure for ap_article_content
-- ----------------------------
DROP TABLE IF EXISTS `ap_article_content`;
CREATE TABLE `ap_article_content` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `article_id` bigint(20) unsigned DEFAULT NULL COMMENT '文章ID',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1383828014650150915 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP已发布文章内容表';

-- ----------------------------
-- Records of ap_article_content
-- ----------------------------
INSERT INTO `ap_article_content` VALUES ('1302862388957036545', '1302862387124125698', '[{\"type\":\"text\",\"value\":\"Java语言是美国Sun公司（Stanford University Network），在1995年推出的高级的编程语言。所谓编程语言，是计算机的语言，人们可以使用编程语言对计算机下达命令，让计算机完成人们需要的功能。\\n\\n2009年，Sun公司被甲骨文公司收购，所以我们现在访问oracle官网即可：https://www.oracle.com\\nJava语言共同创始人之一：詹姆斯·高斯林 （James Gosling），被称为“Java之父”\\n\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl9V2CqAZe18AAOoOOsvWPc041.png\"},{\"type\":\"text\",\"value\":\"Java语言发展历史\\n\\n- 1995年Sun公司推出Java语言\\n- 1996年发布Java 1.0版本\\n- 1997年发布Java 1.1版本\\n- 1998年发布Java 1.2版本\\n- 2000年发布Java 1.3版本\\n- 2002年发布Java 1.4版本\\n- 2004年发布Java 5.0版本\\n- 2006年发布Java 6.0版本\\n- 2009年Oracle甲骨文公司收购Sun公司\\n- 2011年发布Java 7.0版本\\n- 2014年发布Java 8.0版本\\n- 2017年9月发布Java 9.0版本\\n- 2018年3月发布Java 10.0版本\\n- 2018年9月发布Java 11.0版本\\n\"},{\"type\":\"text\",\"value\":\"Java 语言的三个版本\\n\\n- JavaSE：标准版，用于桌面应用的开发，是其他两个版本的基础。\\n  - 学习JavaSE的目的, 是为了就业班要学习的JavaEE打基础.\\n\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl9V2F6AdQxAAAGyaOdp4gk784.png\"},{\"type\":\"text\",\"value\":\"- JavaEE：企业版，用于Web方向的网站开发\\n  - 网站：网页 + 后台服务器\\n\\nJava语言主要应用在互联网程序的开发领域。常见的互联网程序比如天猫、京东、物流系统、网银系统等，以及服务器后台处理大数据的存储、查询、数据挖掘等也有很多应用。\\n\"}]');
INSERT INTO `ap_article_content` VALUES ('1302864438885367810', '1302864436297482242', '[{\"type\":\"text\",\"value\":\"Java虚拟机——JVM\\n\\n- JVM（Java Virtual Machine ）：Java虚拟机，简称JVM，是运行所有Java程序的假想计算机，是Java程序的运行环境，是Java 最具吸引力的特性之一。我们编写的Java代码，都运行在JVM 之上。\\n- 跨平台：任何软件的运行，都必须要运行在操作系统之上，而我们用Java编写的软件可以运行在任何的操作系统上，这个特性称为Java语言的跨平台特性。该特性是由JVM实现的，我们编写的程序运行在JVM上，而JVM运行在操作系统上。\\n\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl9V2n6AArZsAAGMmaPdt7w502.png\"},{\"type\":\"text\",\"value\":\"如图所示，Java的虚拟机本身不具备跨平台功能的，每个操作系统下都有不同版本的虚拟机。\\n\\n问题1: Java 是如何实现跨平台的呢？\\n\\n- 答：因为在不同操作系统中都安装了对应版本的 JVM 虚拟机\\n- 注意: Java程序想要运行, 必须依赖于JVM虚拟机.\\n\\n问题2: JVM 本身是否允许跨平台呢？\\n\\n- 答：不允许，允许跨平台的是 Java 程序，而不是虚拟机。\\n\"}]');
INSERT INTO `ap_article_content` VALUES ('1302864732679585794', '1302864730402078722', '[{\"type\":\"text\",\"value\":\"这些都是测试这些都是测试这些都是测试这些都是测试这些都是测试这些都是测试\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg\"},{\"type\":\"text\",\"value\":\"这些都是测试这些都是测试这些都是测试这些都是测试\"}]');
INSERT INTO `ap_article_content` VALUES ('1302865011026182145', '1302865008438296577', '[{\"type\":\"text\",\"value\":\"过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892uyAR12rAADi7UxPXeM267.jpg\"},{\"type\":\"text\",\"value\":\"过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中过山车故障20名游客倒挂空中\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1302865308704325633', '1302865306489733122', '[{\"type\":\"text\",\"value\":\"武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg\"},{\"type\":\"text\",\"value\":\"武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国武汉高校开学典礼万人歌唱祖国v\"}]');
INSERT INTO `ap_article_content` VALUES ('1302865476799447041', '1302865474094120961', '[{\"type\":\"text\",\"value\":\"天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892tyAFc60AAMUNUuOKPA619.jpg\"},{\"type\":\"text\",\"value\":\"天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔天降铁球砸死女婴整栋楼被判赔vv\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1302977181835599873', '1302977178887004162', '[{\"type\":\"text\",\"value\":\"央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路央视曝光境外医疗豪华旅游套路\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892wqAANwOAAJW8oQUlAc087.jpg\"}]');
INSERT INTO `ap_article_content` VALUES ('1302977460907810818', '1302977458215067649', '[{\"type\":\"text\",\"value\":\"510多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892vOASiunAAGzs3UZ1Cg252.jpg\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892uyAR12rAADi7UxPXeM267.jpg\"},{\"type\":\"text\",\"value\":\"10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童v\"},{\"type\":\"text\",\"value\":\"请10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童10多名陌生人合力托举悬窗女童v\"}]');
INSERT INTO `ap_article_content` VALUES ('1302977561034235906', '1302977558807060482', '[{\"type\":\"text\",\"value\":\"杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892wKAZLhtAASZUi49De0836.jpg\"},{\"type\":\"text\",\"value\":\"杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸杨澜回应一秒变脸\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1302977755742216193', '1302977754114826241', '[{\"type\":\"text\",\"value\":\"3黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl892vuAXr_MAASCMYD0yzc919.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应黄龄工作室发视频回应\"}]');
INSERT INTO `ap_article_content` VALUES ('1303156151151493121', '1303156149041758210', '[{\"type\":\"image\",\"value\":\"http://192.168.200.130/group1/M00/00/00/wKjIgl9W6iOAD2doAAFY4E1K7-g384.png\"},{\"type\":\"text\",\"value\":\"全国抗击新冠肺炎疫情表彰大会开始15家“文化会客厅”展现产业发展的集群效应全球疫情简报:印度新冠确诊病例超420万 升至全球第二中方提出《全球数据安全倡议》\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827787742498817', '1383827787629252610', '[{\"type\":\"text\",\"value\":\"Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827888833613826', '1383827888816836609', '[{\"type\":\"text\",\"value\":\"经常听到开发人员抱怨 ，“这么烂的代码，我来重构一下！”，“这代码怎么能这么写呢？谁来重构一下？”，“这儿有个坏味道，重构吧！”\\n\\n作为一名项目经理，每次听到“重构”两个字，既想给追求卓越代码的开发人员点个赞，同时又会感觉非常紧张，为什么又要重构？马上就要上线了，怎么还要改？是不是应该阻止开发人员做重构？\\n\\n重构几乎是开发人员最喜欢的一项实践了，可项目经理们却充满了顾虑，那么为什么项目经理不喜欢重构呢？\\n\\n老功能被破坏\\n不止一次遇到这样的场景，某一天一个老功能突然被破坏了，项目经理们感到奇怪，产品这块儿的功能已经很稳定了，也没有在这部分开发什么新功能，为什么突然出问题了呢？\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827911826788353', '1383827911810011137', '[{\"type\":\"text\",\"value\":\"我在面试过程中，问过很多高级java工程师，是否了解Base64？部分人回答了解，部分人直接回答不了解。而说了解的那部分人却回答不上来它的原理。\\n\\nBase64 的由来\\nBase64是网络上最常见的用于传输8Bit字节代码的编码方式之一，大家可以查看RFC2045～RFC2049，上面有MIME的详细规范。它是一种基于用64个可打印字符来表示二进制数据的表示方法。它通常用作存储、传输一些二进制数据编码方法！也是MIME（多用途互联网邮件扩展，主要用作电子邮件标准）中一种可打印字符表示二进制数据的常见编码方法！它其实只是定义用可打印字符传输内容一种方法，并不会产生新的字符集！\\n\\n传统的邮件只支持可见字符的传送，像ASCII码的控制字符就 不能通过邮件传送。这样用途就受到了很大的限制，比如图片二进制流的每个字节不可能全部是可见字符，所以就传送不了。最好的方法就是在不改变传统协议的情 况下，做一种扩展方案来支持二进制文件的传送。把不可打印的字符也能用可打印字符来表示，问题就解决了。Base64编码应运而生，Base64就是一种 基于64个可打印字符来表示二进制数据的表示方法。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/b44c65376f12498e873223d9d6fdf523.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827952335376385', '1383827952326987778', '[{\"type\":\"text\",\"value\":\"根据经验来看，jdk api 一般推荐的线程数为CPU核数的2倍。但是有些书籍要求可以设置为CPU核数的8倍，也有的业务设置为CPU核数的32倍。\\n“工作线程数”的设置依据是什么，到底设置为多少能够最大化CPU性能，是本文要讨论的问题。\\n\\n工作线程数是不是设置的越大越好\\n显然不是的。使用java.lang.Thread类或者java.lang.Runnable接口编写代码来定义、实例化和启动新线程。\\n一个Thread类实例只是一个对象，像Java中的任何其他对象一样，具有变量和方法，生死于堆上。\\nJava中，每个线程都有一个调用栈，即使不在程序中创建任何新的线程，线程也在后台运行着。\\n一个Java应用总是从main()方法开始运行，main()方法运行在一个线程内，它被称为主线程。\\n一旦创建一个新的线程，就产生一个新的调用栈。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/a3f0bc438c244f788f2df474ed8ecdc1.jpg\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827976322600963', '1383827976310018049', '[{\"type\":\"text\",\"value\":\"通过上篇《IT培训就业艰难，行业乱象频发，如何破解就业难题?》一文，相信大家已初步了解“项目课程”对程序员能否就业且高薪就业的重要性。\\n\\n　　因此，小白在选择IT培训机构时，关注的重点就在于所学“项目课程”能否真正帮你增加就业筹码。当然，前提必须是学到“真”项目。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/1818283261e3401892e1383c1bd00596.jpg\"}]');
INSERT INTO `ap_article_content` VALUES ('1383827995834503170', '1383827995813531650', '[{\"type\":\"text\",\"value\":\"在选择IT培训机构时，你应该有注意到，很多机构都将“项目课程”作为培训中的重点。那么，为什么要学习项目课程?为什么项目课程才是IT培训课程的核心?\\n\\n　　1\\n\\n　　在这个靠“技术经验说话”的IT行业里，假如你是一个计算机或IT相关专业毕业生，在没有实际项目开发经验的情况下，“找到第一份全职工作”可能是你职业生涯中遇到的最大挑战。\\n\\n　　为什么说找第一份工作很难?\\n\\n　　主要在于：实际企业中用到的软件开发知识和在学校所学的知识是完全不同的。假设你已经在学校和同学做过周期长达2-3个月的项目，但真正工作中的团队协作与你在学校中经历的协作也有很多不同。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg\"},{\"type\":\"text\",\"value\":\"在实际团队中，每一位成员彼此团结一致，为项目的交付而努力，这也意味着你必须要理解好在项目中负责的那部分任务，在规定时间交付还需确保你负责的功能，在所有环境中都能很好地发挥作用，而不仅仅是你的本地机器。\\n\\n　　这需要你对项目中的每一行代码严谨要求。学校练习的项目中，对bug的容忍度很大，而在实际工作中是绝对不能容忍的。项目中的任何一个环节都涉及公司利益，任何一个bug都可能影响公司的收入及形象。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/c7c3d36d25504cf6aecdcd5710261773.jpg\"}]');
INSERT INTO `ap_article_content` VALUES ('1383828014650150914', '1383828014629179393', '[{\"type\":\"text\",\"value\":\"找工作，企业重点问的是项目经验，更是HR筛选的“第一门槛”，直接决定了你是否有机会进入面试环节。\\n\\n　　项目经验更是评定“个人能力/技能”真实性的“证据”，反映了求职者某个方面的实际动手能力、对某个领域或某种技能的掌握程度。\"},{\"type\":\"image\",\"value\":\"http://192.168.200.130:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg\"},{\"type\":\"text\",\"value\":\"很多经过培训期望快速上岗的程序员，靠着培训机构“辅导”顺利经过面试官对于“项目经验”的考核上岗后，在面对“有限时间”“复杂业务”“新项目需求”等多项标签加持的工作任务，却往往不知从何下手或开发进度极其缓慢。最终结果就是：熬不过试用期。\\n\\n　　从而也引发了企业对于“培训出身程序员”的“有色眼光”。你甚至也一度怀疑“IT培训班出来的人真的不行吗?”\"}]');

-- ----------------------------
-- Table structure for ap_author
-- ----------------------------
DROP TABLE IF EXISTS `ap_author`;
CREATE TABLE `ap_author` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) DEFAULT NULL COMMENT '作者名称',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '0 爬取数据\r\n            1 签约合作商\r\n            2 平台自媒体人\r\n            ',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '社交账号ID',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `wm_user_id` int(11) unsigned DEFAULT NULL COMMENT '自媒体账号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_type_name` (`type`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='APP文章作者信息表';

-- ----------------------------
-- Records of ap_author
-- ----------------------------
INSERT INTO `ap_author` VALUES ('1', 'zhangsan', '2', '1', '2020-03-19 23:43:54', null);
INSERT INTO `ap_author` VALUES ('2', 'lisi', '2', '2', '2020-03-19 23:47:44', null);
INSERT INTO `ap_author` VALUES ('3', 'wangwu', '2', '3', '2020-03-19 23:50:09', null);
INSERT INTO `ap_author` VALUES ('4', 'admin', '2', '4', '2020-03-30 16:36:41', null);

-- ----------------------------
-- Table structure for ap_collection
-- ----------------------------
DROP TABLE IF EXISTS `ap_collection`;
CREATE TABLE `ap_collection` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `entry_id` int(11) unsigned DEFAULT NULL COMMENT '实体ID',
  `article_id` bigint(20) unsigned DEFAULT NULL COMMENT '文章ID',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '点赞内容类型\r\n            0文章\r\n            1动态',
  `collection_time` datetime DEFAULT NULL COMMENT '创建时间',
  `published_time` datetime DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_type` (`entry_id`,`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='APP收藏信息表';

-- ----------------------------
-- Records of ap_collection
-- ----------------------------
INSERT INTO `ap_collection` VALUES ('1', '1', '1303156149041758210', '0', '2020-04-07 23:46:47', '2020-04-07 23:46:50');
```

### 创建用户模块

在 leadnews-service 模块下面创建子模块 leadnews-user，该模块即用户模块。

```java
package com.linxuan.user;

@SpringBootApplication
// 配置注册中心
@EnableDiscoveryClient
@MapperScan("com.linxuan.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
```

```yml
# bootstrap.yml文件，放在resources目录下面
# 项目启动会首先读取bootstrap.yml文件，从nacos中获取配置，读取nacos中配置文件。之后加载本地application.yml文件并合并，这样配置就读取完毕了
server:
  port: 51801
spring:
  application:
    name: leadnews-user
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

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- resources目录西面创建logback.xml文件配置日志 -->

<configuration>
    <!--定义日志文件的存储地址,使用绝对路径-->
    <property name="LOG_HOME" value="D:/logs"/>

    <!-- Console 输出设置 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <!-- 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <fileNamePattern>${LOG_HOME}/leadnews.%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 异步输出 -->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>512</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="FILE"/>
    </appender>


    <logger name="org.apache.ibatis.cache.decorators.LoggingCache" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
    <logger name="org.springframework.boot" level="debug"/>
    <root level="info">
        <!--<appender-ref ref="ASYNC"/>-->
        <appender-ref ref="FILE"/>
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

在虚拟机 Docker 中的 nacos 中创建如下配置，Data Id = leadnews-user。

```yml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_user?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.linxaun.model.user.pojos
```

### 用户登录代码

|          | 说明                     |
| -------- | ------------------------ |
| 接口路径 | /api/v1/login/login_auth |
| 请求方式 | POST                     |
| 参数     | LoginDto                 |
| 响应结果 | ResponseResult           |

```java
package com.linxuan.model.user.dtos;

@Data
public class LoginDto {

    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
}
```

```java
package com.linxuan.user.mapper;

public interface ApUserMapper extends BaseMapper<ApUser> {
}
```

```java
package com.linxuan.user.service;

public interface ApUserService extends IService<ApUser> {

    /**
     * app端用户登录功能
     * @param loginDto 前端参数
     * @return
     */
    public ResponseResult login(LoginDto loginDto);
}
```

```java
package com.linxuan.user.service.impl;

@Slf4j
@Service
@Transactional
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser>
        implements ApUserService {

    /**
     * app端用户登录功能
     *
     * @param loginDto 前端参数
     * @return
     */
    @Override
    public ResponseResult login(LoginDto loginDto) {
        if (StringUtils.isNotBlank(loginDto.getPhone()) && StringUtils.isNotBlank(loginDto.getPassword())) {
            // 判断用户是否存在
            ApUser dbUser = getOne(new LambdaQueryWrapper<ApUser>().eq(ApUser::getPhone, loginDto.getPhone()));
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST, "用户不存在");
            }

            // 用户存在校验密码
            String password = loginDto.getPassword();
            String salt = dbUser.getSalt();
            String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pwd.equals(dbUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码错误");
            }

            // 密码正确，用户通过校验，返回JWT
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            dbUser.setPassword("");
            dbUser.setSalt("");
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        }

        // 游客登录，JWT设置为0，直接返回即可
        Map<String, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(0L));
        return ResponseResult.okResult(map);
    }
}
```

```java
package com.linxuan.user.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserServiceImpl apUserService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto){

        return apUserService.login(loginDto);
    }
}
```

测试：http://localhost:51801/api/v1/login/login_auth

```json
// Body栏，Row，JSON格式
// 密码使用admin，只有用户知道。后台密码列存储的是MD5+盐加密后的数据，admin是初始密码。
{
    "phone":"13511223456",
    "password":"admin"
}
```

### 登录网关校验

网关的核心功能特性：权限控制、请求路由、 限流。

- 权限控制：网关为微服务入口，需要校验用户是是否有请求资格，如果没有则进行拦截。
- 路由和负载均衡：一切请求都必须先经过 gateway，但网关不处理业务，而是根据某种规则，把请求转发到某个微服务，这个过程叫做路由。当然路由的目标服务有多个时，还需要做负载均衡。
- 限流：当请求流量过高时，在网关中按照下流的微服务能够接受的速度来放行请求，避免服务压力过大。

Spring Cloud Gateway 是 Spring Cloud 的一个全新项目，该项目是基于 Spring 5.0，Spring Boot 2.0 和 Project Reactor 等响应式编程和事件流技术开发的网关，它旨在为微服务架构提供一种简单有效的统一的 API 路由管理方式。

Gateway 网关是我们服务的守门神，所有微服务的统一入口。

在 leadnews-gateway 模块下面创建 leadnews-app-gateway 子模块，该子模块是用户的网关校验。

```xml
<!--  leadnews-gateway 模块的pom.xml添加，这样子模块就不用重复添加了 -->
<dependencies>
    <!-- 网关起步依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <!-- nacos注册中心和配置中心 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <!-- jwt解析 -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
    </dependency>
</dependencies>
```

```java
package com.linxuan.app.gateway;

@SpringBootApplication
@EnableDiscoveryClient
public class AppGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppGatewayApplication.class, args);
    }
}
```

```yml
# bootstrap.yml文件添加如下内容
server:
  port: 51601
spring:
  application:
    name: leadnews-app-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.88.129:8848
      config:
        server-addr: 192.168.88.129:8848
        file-extension: yml
```

nacos 中创建配置Data Id = leadnews-app-gateway

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
```

PostMan测试：http://localhost:51601/user/api/v1/login/login_auth

```json
// Body栏，Row，JSON格式
// 密码使用admin，只有用户知道。后台密码列存储的是MD5+盐加密后的数据，admin是初始密码。
{
    "phone":"13511223456",
    "password":"admin"
}
```

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
            // 这里AppJwtUtil这个工具类需要复制到该模块下面
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                // 返回状态码校验失败
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            // 返回状态码校验失败
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }
}
```

### 部署用户前端

Nginx 具体应用如下：部署静态资源、反向代理、负载均衡。

* 部署静态资源：Nginx 可以作为静态web服务器来部署静态资源。静态资源指在服务端真实存在并且能够直接展示的一些文件，比如常见的html页面、css文件、js文件、图片、视频等资源。
* 反向代理：用户不需要知道目标服务器的地址，也无须在用户端作任何设定。
* 负载均衡：使用负载均衡器将用户请求根据对应的负载均衡算法分发到应用集群中的一台服务器进行处理

首先部署静态资源，这里将静态资源放在 `D:\Java\IdeaProjects\lead_news\front-workspce`目录下面，需要在 Nginx 配置文件中指定其位置。

因为后续不仅要部署三端（用户端、作者端、管理端）的前端项目，所以这里额外创建配置文件，然后将配置文件导入 nginx.conf 即可！

在 nginx 的 conf 目录下创建 leadnews.conf 文件夹用于存放该项目的配置文件，首先创建 leadnews-app.conf

```bash
# upstream定义一组服务器，名称叫做linxuan-app-gateway
upstream  linxuan-app-gateway{
    server localhost:51601;
}

server {
    # 前端项目监听端口号
	listen 8801;
	location / {
	    # 定义前端项目资源路径
		root D:/Java/IdeaProjects/lead_news/front-workspce/app-web/;
		# 索引文件
		index index.html;
	}
	
	location ~/app/(.*) {
	    # 反向代理配置
		proxy_pass http://linxuan-app-gateway/$1;
		proxy_set_header HOST $host;  # 不改变源请求头的值
		proxy_pass_request_body on;  #开启获取请求体
		proxy_pass_request_headers on;  #开启获取请求头
		proxy_set_header X-Real-IP $remote_addr;   # 记录真实发出请求的客户端IP
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  #记录代理信息
	}
}
```

在原本的 nginx.conf 引入配置文件

```sh
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

	# 引入自定义配置文件
	include leadnews.conf/*.conf;
}
```

```bash
# 查看nginx版本
E:\nginx\nginx-1.18.0>nginx -v
nginx version: nginx/1.18.0

# 启动nginx
E:\nginx\nginx-1.18.0>start nginx

# 重新加载nginx
E:\nginx\nginx-1.18.0>nginx -s reload

# 停止nginx
E:\nginx\nginx-1.18.0>nginx -s stop
```

使用 cmd 命令重新加载配置文件 `nginx -s reload` ，[测试前端项目](http://localhost:8801)
