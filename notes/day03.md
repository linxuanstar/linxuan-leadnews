# 自媒体端服务

## 基础环境搭建

### 数据库配置

| leadnews_wemedia库中表名称 | 自媒体库说明             |
| -------------------------- | ------------------------ |
| wm_channel                 | 频道信息表               |
| wm_fans_statistics         | 自媒体粉丝数据统计表     |
| wm_material                | 自媒体图文素材信息表     |
| wm_news                    | 自媒体图文内容信息表     |
| wm_news_material           | 自媒体图文引用素材信息表 |
| wm_news_statistics         | 自媒体图文数据统计表     |
| wm_user                    | 自媒体用户信息表         |

导入表 leadnews_wemedia.sql

```sql
# 命令行导入sql文件，路径不能加上双引号
mysql> source D:\Java\IdeaProjects\lead_news\!information\day03\leadnews_wemedia.sql
```

```sql
CREATE DATABASE IF NOT EXISTS leadnews_wemedia DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadnews_wemedia;
SET NAMES utf8;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for wm_channel
-- ----------------------------
DROP TABLE IF EXISTS `wm_channel`;
CREATE TABLE `wm_channel` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频道名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频道描述',
  `is_default` tinyint(1) unsigned DEFAULT NULL COMMENT '是否默认频道',
  `status` tinyint(1) unsigned DEFAULT NULL,
  `ord` tinyint(3) unsigned DEFAULT NULL COMMENT '默认排序',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='频道信息表';

-- ----------------------------
-- Records of wm_channel
-- ----------------------------
INSERT INTO `wm_channel` VALUES ('0', '其他', '其他', '1', '1', '12', '2021-04-18 10:55:41');
INSERT INTO `wm_channel` VALUES ('1', 'java', '后端框架', '1', '1', '1', '2021-04-18 12:25:30');
INSERT INTO `wm_channel` VALUES ('2', 'Mysql', '轻量级数据库', '1', '1', '4', '2021-04-18 10:55:41');
INSERT INTO `wm_channel` VALUES ('3', 'Vue', '阿里前端框架', '1', '1', '5', '2021-04-18 10:55:41');
INSERT INTO `wm_channel` VALUES ('4', 'Python', '未来的语言', '1', '1', '6', '2021-04-18 10:55:41');
INSERT INTO `wm_channel` VALUES ('5', 'Weex', '向未来致敬', '1', '1', '7', '2021-04-18 10:55:41');
INSERT INTO `wm_channel` VALUES ('6', '大数据', '不会，则不要说自己是搞互联网的', '1', '1', '10', '2021-04-18 10:55:41');

-- ----------------------------
-- Table structure for wm_fans_statistics
-- ----------------------------
DROP TABLE IF EXISTS `wm_fans_statistics`;
CREATE TABLE `wm_fans_statistics` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '主账号ID',
  `article` int(11) unsigned DEFAULT NULL COMMENT '子账号ID',
  `read_count` int(11) unsigned DEFAULT NULL,
  `comment` int(11) unsigned DEFAULT NULL,
  `follow` int(11) unsigned DEFAULT NULL,
  `collection` int(11) unsigned DEFAULT NULL,
  `forward` int(11) unsigned DEFAULT NULL,
  `likes` int(11) unsigned DEFAULT NULL,
  `unlikes` int(11) unsigned DEFAULT NULL,
  `unfollow` int(11) unsigned DEFAULT NULL,
  `burst` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` date DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_user_id_time` (`user_id`,`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体粉丝数据统计表';

-- ----------------------------
-- Records of wm_fans_statistics
-- ----------------------------

-- ----------------------------
-- Table structure for wm_material
-- ----------------------------
DROP TABLE IF EXISTS `wm_material`;
CREATE TABLE `wm_material` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '素材类型\r\n            0 图片\r\n            1 视频',
  `is_collection` tinyint(1) DEFAULT NULL COMMENT '是否收藏',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文素材信息表';

-- ----------------------------
-- Records of wm_material
-- ----------------------------
INSERT INTO `wm_material` VALUES ('67', '1102', 'http://192.168.88.129:9000/leadnews/2021/04/26/a73f5b60c0d84c32bfe175055aaaac40.jpg', '0', '0', '2021-04-26 00:14:01');
INSERT INTO `wm_material` VALUES ('68', '1102', 'http://192.168.88.129:9000/leadnews/2021/04/26/d4f6ef4c0c0546e69f70bd3178a8c140.jpg', '0', '0', '2021-04-26 00:18:20');
INSERT INTO `wm_material` VALUES ('69', '1102', 'http://192.168.88.129:9000/leadnews/2021/04/26/5ddbdb5c68094ce393b08a47860da275.jpg', '0', '0', '2021-04-26 00:18:27');
INSERT INTO `wm_material` VALUES ('70', '1102', 'http://192.168.88.129:9000/leadnews/2021/04/26/9f8a93931ab646c0a754475e0c4b0a98.jpg', '0', '0', '2021-04-26 00:18:34');
INSERT INTO `wm_material` VALUES ('71', '1102', 'http://192.168.88.129:9000/leadnews/2021/04/26/ef3cbe458db249f7bd6fb4339e593e55.jpg', '0', '0', '2021-04-26 00:18:39');

-- ----------------------------
-- Table structure for wm_news
-- ----------------------------
DROP TABLE IF EXISTS `wm_news`;
CREATE TABLE `wm_news` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `title` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '图文内容',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '文章布局\r\n            0 无图文章\r\n            1 单图文章\r\n            3 多图文章',
  `channel_id` int(11) unsigned DEFAULT NULL COMMENT '图文频道ID',
  `labels` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `submited_time` datetime DEFAULT NULL COMMENT '提交时间',
  `status` tinyint(2) unsigned DEFAULT NULL COMMENT '当前状态\r\n            0 草稿\r\n            1 提交（待审核）\r\n            2 审核失败\r\n            3 人工审核\r\n            4 人工审核通过\r\n            8 审核通过（待发布）\r\n            9 已发布',
  `publish_time` datetime DEFAULT NULL COMMENT '定时发布时间，不定时则为空',
  `reason` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝理由',
  `article_id` bigint(20) unsigned DEFAULT NULL COMMENT '发布库文章ID',
  `images` longtext COLLATE utf8mb4_unicode_ci COMMENT '//图片用逗号分隔',
  `enable` tinyint(1) unsigned DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文内容信息表';

-- ----------------------------
-- Records of wm_news
-- ----------------------------
INSERT INTO `wm_news` VALUES ('6225', '1102', '“真”项目课程对找工作有什么帮助？', '[{\"type\":\"text\",\"value\":\"找工作，企业重点问的是项目经验，更是HR筛选的“第一门槛”，直接决定了你是否有机会进入面试环节。\\n\\n　　项目经验更是评定“个人能力/技能”真实性的“证据”，反映了求职者某个方面的实际动手能力、对某个领域或某种技能的掌握程度。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg\"},{\"type\":\"text\",\"value\":\"很多经过培训期望快速上岗的程序员，靠着培训机构“辅导”顺利经过面试官对于“项目经验”的考核上岗后，在面对“有限时间”“复杂业务”“新项目需求”等多项标签加持的工作任务，却往往不知从何下手或开发进度极其缓慢。最终结果就是：熬不过试用期。\\n\\n　　从而也引发了企业对于“培训出身程序员”的“有色眼光”。你甚至也一度怀疑“IT培训班出来的人真的不行吗?”\"}]', '1', '1', '项目课程', '2021-04-19 00:08:10', '2021-04-19 00:08:10', '9', '2021-04-19 00:08:05', '审核成功', '1383828014629179393', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg', '1');
INSERT INTO `wm_news` VALUES ('6226', '1102', '学IT，为什么要学项目课程？', '[{\"type\":\"text\",\"value\":\"在选择IT培训机构时，你应该有注意到，很多机构都将“项目课程”作为培训中的重点。那么，为什么要学习项目课程?为什么项目课程才是IT培训课程的核心?\\n\\n　　1\\n\\n　　在这个靠“技术经验说话”的IT行业里，假如你是一个计算机或IT相关专业毕业生，在没有实际项目开发经验的情况下，“找到第一份全职工作”可能是你职业生涯中遇到的最大挑战。\\n\\n　　为什么说找第一份工作很难?\\n\\n　　主要在于：实际企业中用到的软件开发知识和在学校所学的知识是完全不同的。假设你已经在学校和同学做过周期长达2-3个月的项目，但真正工作中的团队协作与你在学校中经历的协作也有很多不同。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg\"},{\"type\":\"text\",\"value\":\"在实际团队中，每一位成员彼此团结一致，为项目的交付而努力，这也意味着你必须要理解好在项目中负责的那部分任务，在规定时间交付还需确保你负责的功能，在所有环境中都能很好地发挥作用，而不仅仅是你的本地机器。\\n\\n　　这需要你对项目中的每一行代码严谨要求。学校练习的项目中，对bug的容忍度很大，而在实际工作中是绝对不能容忍的。项目中的任何一个环节都涉及公司利益，任何一个bug都可能影响公司的收入及形象。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/c7c3d36d25504cf6aecdcd5710261773.jpg\"}]', '3', '1', '项目课程', '2021-04-19 00:13:58', '2021-04-19 00:13:58', '9', '2021-04-19 00:10:48', '审核成功', '1383827995813531650', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/7d0911a41a3745efa8509a87f234813c.jpg,http://192.168.88.129:9000/leadnews/2021/4/20210418/c7c3d36d25504cf6aecdcd5710261773.jpg,http://192.168.88.129:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg', '1');
INSERT INTO `wm_news` VALUES ('6227', '1102', '小白如何辨别其真与伪&好与坏？', '[{\"type\":\"text\",\"value\":\"通过上篇《IT培训就业艰难，行业乱象频发，如何破解就业难题?》一文，相信大家已初步了解“项目课程”对程序员能否就业且高薪就业的重要性。\\n\\n　　因此，小白在选择IT培训机构时，关注的重点就在于所学“项目课程”能否真正帮你增加就业筹码。当然，前提必须是学到“真”项目。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/1818283261e3401892e1383c1bd00596.jpg\"}]', '1', '1', '小白', '2021-04-19 00:15:05', '2021-04-19 00:15:05', '9', '2021-04-19 00:14:58', '审核成功', '1383827976310018049', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/1818283261e3401892e1383c1bd00596.jpg', '1');
INSERT INTO `wm_news` VALUES ('6228', '1102', '工作线程数是不是设置的越大越好', '[{\"type\":\"text\",\"value\":\"根据经验来看，jdk api 一般推荐的线程数为CPU核数的2倍。但是有些书籍要求可以设置为CPU核数的8倍，也有的业务设置为CPU核数的32倍。\\n“工作线程数”的设置依据是什么，到底设置为多少能够最大化CPU性能，是本文要讨论的问题。\\n\\n工作线程数是不是设置的越大越好\\n显然不是的。使用java.lang.Thread类或者java.lang.Runnable接口编写代码来定义、实例化和启动新线程。\\n一个Thread类实例只是一个对象，像Java中的任何其他对象一样，具有变量和方法，生死于堆上。\\nJava中，每个线程都有一个调用栈，即使不在程序中创建任何新的线程，线程也在后台运行着。\\n一个Java应用总是从main()方法开始运行，main()方法运行在一个线程内，它被称为主线程。\\n一旦创建一个新的线程，就产生一个新的调用栈。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/a3f0bc438c244f788f2df474ed8ecdc1.jpg\"}]', '1', '1', '11', '2021-04-19 00:16:57', '2021-04-19 00:16:57', '9', '2021-04-19 00:16:52', '审核成功', '1383827952326987778', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/a3f0bc438c244f788f2df474ed8ecdc1.jpg', '1');
INSERT INTO `wm_news` VALUES ('6229', '1102', 'Base64编解码原理', '[{\"type\":\"text\",\"value\":\"我在面试过程中，问过很多高级java工程师，是否了解Base64？部分人回答了解，部分人直接回答不了解。而说了解的那部分人却回答不上来它的原理。\\n\\nBase64 的由来\\nBase64是网络上最常见的用于传输8Bit字节代码的编码方式之一，大家可以查看RFC2045～RFC2049，上面有MIME的详细规范。它是一种基于用64个可打印字符来表示二进制数据的表示方法。它通常用作存储、传输一些二进制数据编码方法！也是MIME（多用途互联网邮件扩展，主要用作电子邮件标准）中一种可打印字符表示二进制数据的常见编码方法！它其实只是定义用可打印字符传输内容一种方法，并不会产生新的字符集！\\n\\n传统的邮件只支持可见字符的传送，像ASCII码的控制字符就 不能通过邮件传送。这样用途就受到了很大的限制，比如图片二进制流的每个字节不可能全部是可见字符，所以就传送不了。最好的方法就是在不改变传统协议的情 况下，做一种扩展方案来支持二进制文件的传送。把不可打印的字符也能用可打印字符来表示，问题就解决了。Base64编码应运而生，Base64就是一种 基于64个可打印字符来表示二进制数据的表示方法。\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/b44c65376f12498e873223d9d6fdf523.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]', '1', '1', '11', '2021-04-19 00:17:44', '2021-04-19 00:17:44', '9', '2021-04-19 00:17:42', '审核成功', '1383827911810011137', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/b44c65376f12498e873223d9d6fdf523.jpg', '1');
INSERT INTO `wm_news` VALUES ('6230', '1102', '为什么项目经理不喜欢重构？', '[{\"type\":\"text\",\"value\":\"经常听到开发人员抱怨 ，“这么烂的代码，我来重构一下！”，“这代码怎么能这么写呢？谁来重构一下？”，“这儿有个坏味道，重构吧！”\\n\\n作为一名项目经理，每次听到“重构”两个字，既想给追求卓越代码的开发人员点个赞，同时又会感觉非常紧张，为什么又要重构？马上就要上线了，怎么还要改？是不是应该阻止开发人员做重构？\\n\\n重构几乎是开发人员最喜欢的一项实践了，可项目经理们却充满了顾虑，那么为什么项目经理不喜欢重构呢？\\n\\n老功能被破坏\\n不止一次遇到这样的场景，某一天一个老功能突然被破坏了，项目经理们感到奇怪，产品这块儿的功能已经很稳定了，也没有在这部分开发什么新功能，为什么突然出问题了呢？\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/e8113ad756a64ea6808f91130a6cd934.jpg\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]', '1', '1', '11', '2021-04-19 00:19:23', '2021-04-19 00:19:23', '9', '2021-04-19 00:19:09', '审核成功', '1383827888816836609', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg', '1');
INSERT INTO `wm_news` VALUES ('6231', '1102', 'Kafka文件的存储机制', '[{\"type\":\"text\",\"value\":\"Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制Kafka文件的存储机制\"},{\"type\":\"image\",\"value\":\"http://192.168.88.129:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]', '1', '1', '11', '2021-04-19 00:58:47', '2021-04-19 00:58:47', '9', '2021-04-19 00:20:17', '审核成功', '1383827787629252610', 'http://192.168.88.129:9000/leadnews/2021/4/20210418/4a498d9cf3614570ac0cb2da3e51c164.jpg', '1');

-- ----------------------------
-- Table structure for wm_news_material
-- ----------------------------
DROP TABLE IF EXISTS `wm_news_material`;
CREATE TABLE `wm_news_material` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` int(11) unsigned DEFAULT NULL COMMENT '素材ID',
  `news_id` int(11) unsigned DEFAULT NULL COMMENT '图文ID',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '引用类型\r\n            0 内容引用\r\n            1 主图引用',
  `ord` tinyint(1) unsigned DEFAULT NULL COMMENT '引用排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文引用素材信息表';

-- ----------------------------
-- Records of wm_news_material
-- ----------------------------
INSERT INTO `wm_news_material` VALUES ('255', '61', '6232', '0', '0');
INSERT INTO `wm_news_material` VALUES ('256', '61', '6232', '1', '0');
INSERT INTO `wm_news_material` VALUES ('263', '61', '6231', '0', '0');
INSERT INTO `wm_news_material` VALUES ('264', '61', '6231', '1', '0');
INSERT INTO `wm_news_material` VALUES ('265', '57', '6230', '0', '0');
INSERT INTO `wm_news_material` VALUES ('266', '61', '6230', '0', '1');
INSERT INTO `wm_news_material` VALUES ('267', '61', '6230', '1', '0');
INSERT INTO `wm_news_material` VALUES ('268', '58', '6229', '0', '0');
INSERT INTO `wm_news_material` VALUES ('269', '58', '6229', '1', '0');
INSERT INTO `wm_news_material` VALUES ('270', '62', '6228', '0', '0');
INSERT INTO `wm_news_material` VALUES ('271', '62', '6228', '1', '0');
INSERT INTO `wm_news_material` VALUES ('272', '66', '6227', '0', '0');
INSERT INTO `wm_news_material` VALUES ('273', '66', '6227', '1', '0');
INSERT INTO `wm_news_material` VALUES ('274', '57', '6226', '0', '0');
INSERT INTO `wm_news_material` VALUES ('275', '64', '6226', '0', '1');
INSERT INTO `wm_news_material` VALUES ('276', '65', '6226', '1', '0');
INSERT INTO `wm_news_material` VALUES ('277', '64', '6226', '1', '1');
INSERT INTO `wm_news_material` VALUES ('278', '57', '6226', '1', '2');
INSERT INTO `wm_news_material` VALUES ('279', '65', '6225', '0', '0');
INSERT INTO `wm_news_material` VALUES ('280', '65', '6225', '1', '0');

-- ----------------------------
-- Table structure for wm_news_statistics
-- ----------------------------
DROP TABLE IF EXISTS `wm_news_statistics`;
CREATE TABLE `wm_news_statistics` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '主账号ID',
  `article` int(11) unsigned DEFAULT NULL COMMENT '子账号ID',
  `read_count` int(11) unsigned DEFAULT NULL COMMENT '阅读量',
  `comment` int(11) unsigned DEFAULT NULL COMMENT '评论量',
  `follow` int(11) unsigned DEFAULT NULL COMMENT '关注量',
  `collection` int(11) unsigned DEFAULT NULL COMMENT '收藏量',
  `forward` int(11) unsigned DEFAULT NULL COMMENT '转发量',
  `likes` int(11) unsigned DEFAULT NULL COMMENT '点赞量',
  `unlikes` int(11) unsigned DEFAULT NULL COMMENT '不喜欢',
  `unfollow` int(11) unsigned DEFAULT NULL COMMENT '取消关注量',
  `burst` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` date DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_user_id_time` (`user_id`,`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文数据统计表';

-- ----------------------------
-- Records of wm_news_statistics
-- ----------------------------

-- ----------------------------
-- Table structure for wm_user
-- ----------------------------
DROP TABLE IF EXISTS `wm_user`;
CREATE TABLE `wm_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ap_user_id` int(11) DEFAULT NULL,
  `ap_author_id` int(11) DEFAULT NULL,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录用户名',
  `password` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录密码',
  `salt` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '盐',
  `nickname` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `location` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '归属地',
  `phone` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` tinyint(11) unsigned DEFAULT NULL COMMENT '状态\r\n            0 暂时不可用\r\n            1 永久不可用\r\n            9 正常可用',
  `email` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '账号类型\r\n            0 个人 \r\n            1 企业\r\n            2 子账号',
  `score` tinyint(3) unsigned DEFAULT NULL COMMENT '运营评分',
  `login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体用户信息表';

-- ----------------------------
-- Records of wm_user
-- ----------------------------
INSERT INTO `wm_user` VALUES ('1100', null, null, 'zhangsan', 'ab8c7c1e66a164ab6891b927550ea39a', 'abc', '小张', null, null, '13588996789', '1', null, null, null, '2020-02-17 23:51:15', '2020-02-17 23:51:18');
INSERT INTO `wm_user` VALUES ('1101', null, null, 'lisi', 'a6ecab0c246bbc87926e0fba442cc014', 'def', '小李', null, null, '13234567656', '1', null, null, null, null, null);
INSERT INTO `wm_user` VALUES ('1102', null, null, 'admin', 'a66abb5684c45962d887564f08346e8d', '123456', '管理', null, null, '13234567657', '1', null, null, null, null, '2020-03-14 09:35:13');
INSERT INTO `wm_user` VALUES ('1118', null, null, 'lisi1', '123', '123', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `wm_user` VALUES ('1119', null, null, 'shaseng', '1234', null, null, null, null, null, null, null, null, null, null, null);
```

### 创建实体类

```java
package com.linxuan.model.wemedia.dtos;

@Data
public class WmLoginDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体用户信息表
 * </p>
 */
@Data
@TableName("wm_user")
public class WmUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("ap_user_id")
    private Integer apUserId;

    @TableField("ap_author_id")
    private Integer apAuthorId;

    /**
     * 登录用户名
     */
    @TableField("name")
    private String name;

    /**
     * 登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 盐
     */
    @TableField("salt")
    private String salt;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField("image")
    private String image;

    /**
     * 归属地
     */
    @TableField("location")
    private String location;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 状态
     * 0 暂时不可用
     * 1 永久不可用
     * 9 正常可用
     */
    @TableField("status")
    private Integer status;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 账号类型
     * 0 个人
     * 1 企业
     * 2 子账号
     */
    @TableField("type")
    private Integer type;

    /**
     * 运营评分
     */
    @TableField("score")
    private Integer score;

    /**
     * 最后一次登录时间
     */
    @TableField("login_time")
    private Date loginTime;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
}
```

### 导入自媒体项目

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>leadnews-service</artifactId>
        <groupId>com.linxuan</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>leadnews-wemedia</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

</project>
```

```xml
<!-- leadnews-service的pom.xml，里面定义了管理的子模块 -->
<modules>
    <module>leadnews-user</module>
    <module>leadnews-article</module>
    <module>leadnews-wemedia</module>
</modules>
```

```yml
# bootstrap.yml文件配置
server:
  port: 51803
spring:
  application:
    name: leadnews-wemedia
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.88.129:8848
      config:
        server-addr: 192.168.88.129:8848
        file-extension: yml
```

```yml
# 创建nacos配置leadnews-wemedia，添加如下配置信息：
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    # 自媒体库是leadnews_wemedia
    url: jdbc:mysql://localhost:3306/leadnews_wemedia?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.linxuan.model.article.pojos
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- logback.xml -->
<configuration>
    <!--定义日志文件的存储地址,使用绝对路径-->
    <property name="LOG_HOME" value="e:/logs"/>

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

```java
package com.linxuan.wemedia;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.linxuan.wemedia.mapper")
public class WemediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class,args);
    }
}
```

```java
package com.linxuan.wemedia.mapper;

@Mapper
public interface WmUserMapper extends BaseMapper<WmUser> {
}
```

```java
package com.linxuan.wemedia.service.impl;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    @Override
    public ResponseResult login(WmLoginDto dto) {
        // 1.检查参数
        if (StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码为空");
        }

        // 2.查询用户
        WmUser wmUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, dto.getName()));
        if (wmUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 3.比对密码
        String salt = wmUser.getSalt();
        String pswd = dto.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (pswd.equals(wmUser.getPassword())) {
            // 4.返回数据  jwt
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(wmUser.getId().longValue()));
            wmUser.setSalt("");
            wmUser.setPassword("");
            map.put("user", wmUser);
            return ResponseResult.okResult(map);

        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody WmLoginDto dto){
        return wmUserService.login(dto);
    }
}
```

### 导入自媒体网关

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>leadnews-gateway</artifactId>
        <groupId>com.linxuan</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <!-- 项目名称 -->
    <artifactId>leadnews-wemedia-gateway</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

</project>
```

```xml
<!-- leadnews-gateway的pom.xml，里面定义了管理的子模块 -->
<modules>
    <module>leadnews-app-gateway</module>
    <module>leadnews-wemedia-gateway</module>
</modules>
```

```yml
server:
  port: 51602
spring:
  application:
    name: leadnews-wemedia-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.88.129:8848
      config:
        server-addr: 192.168.88.129:8848
        file-extension: yml
```

```java
package com.linxuan.wemedia.gateway;

@SpringBootApplication
@EnableDiscoveryClient
public class WemediaGatewayAplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaGatewayAplication.class,args);
    }
}
```

```java
package com.linxuan.wemedia.gateway.util;

public class AppJwtUtil {

    // TOKEN的有效期一天（S）
    private static final int TOKEN_TIME_OUT = 3_600;
    // 加密KEY
    private static final String TOKEN_ENCRY_KEY = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 300;

    // 生产ID
    public static String getToken(Long id) {
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id", id);
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))  // 签发时间
                .setSubject("system")  // 说明
                .setIssuer("linxuan") // 签发者信息
                .setAudience("app")  // 接收用户
                .compressWith(CompressionCodecs.GZIP)  // 数据压缩方式
                .signWith(SignatureAlgorithm.HS512, generalKey()) // 加密方式
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))  // 过期时间戳
                .addClaims(claimMaps) // cla信息
                .compact();
    }

    /**
     * 获取token中的claims信息
     *
     * @param token
     * @return
     */
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token);
    }

    /**
     * 获取payload body信息
     *
     * @param token
     * @return
     */
    public static Claims getClaimsBody(String token) throws ExpiredJwtException {
        return getJws(token).getBody();
    }

    /**
     * 获取hearder body信息
     *
     * @param token
     * @return
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 是否过期
     *
     * @param claims
     * @return -1：有效，0：有效，1：过期，2：过期
     */
    public static int verifyToken(Claims claims) throws Exception {
        if (claims == null) {
            return 1;
        }

        claims.getExpiration().before(new Date());
        // 需要自动刷新TOKEN
        if ((claims.getExpiration().getTime() - System.currentTimeMillis()) > REFRESH_TIME * 1000) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(TOKEN_ENCRY_KEY.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
}
```

```java
package com.linxuan.wemedia.gateway.filter;

@Slf4j
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 2.判断是否是登录
        if (request.getURI().getPath().contains("/login")) {
            // 放行
            return chain.filter(exchange);
        }

        // 3.获取token
        String token = request.getHeaders().getFirst("token");

        // 4.判断token是否存在
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            // 是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

```yml
# nacos添加配置leadnews-wemedia-gateway
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]': # 匹配所有请求
            allowedOrigins: "*" #跨域处理 允许所有的域
            allowedMethods: # 支持的方法
              - GET
              - POST
              - PUT
              - DELETE
      routes:
        # 平台管理
        - id: wemedia
          uri: lb://leadnews-wemedia
          predicates:
            - Path=/wemedia/**
          filters:
            - StripPrefix= 1
```

### 自媒体前端搭建

```sh
# nginx-1.18.0/conf/leadnews.conf/leadnews-wemedia.conf文件
# upstream定义一组服务器，名称叫做linxuan-app-gateway
upstream  linxuan-wemedia-gateway{
    # 反向代理到自媒体端网关的51602端口，因为网关也部署在了本地，所以访问本地的51602端口
    server localhost:51602;
}

server {
    # 前端自媒体端监听端口号，访问该端口号才访问该端
	listen 8802;
	location / {
         # 本地前端静态文件地址
		root D:/Java/IdeaProjects/lead_news/front-workspce/wemedia-web/;
		index index.html;
	}
	
	# 请求URL路径以/wemedia/MEDIA/开头激活该代码块，(.*)匹配/wemedia/MEDIA/之后内容，通过$1引用
	location ~/wemedia/MEDIA/(.*) {
		# 将请求的URL代理到的目标URL，最后为http://localhost:51602/$1引用的路径
		proxy_pass http://linxuan-wemedia-gateway/$1;
		proxy_set_header HOST $host;  # 不改变源请求头的值
		proxy_pass_request_body on;  #开启获取请求体
		proxy_pass_request_headers on;  #开启获取请求头
		proxy_set_header X-Real-IP $remote_addr;   # 记录真实发出请求的客户端IP
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  #记录代理信息
	}
}
```

```sh
# nginx-1.18.0/conf/nginx.conf文件配置
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

	# 引入自定义配置文件，leadnews.conf目录下面的所有配置文件全部引入
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

启动项目，[测试](http://localhost:8802/)

## 自媒体素材管理

| leadnews_wemedia库中表名称 | 自媒体库说明             |
| -------------------------- | ------------------------ |
| wm_channel                 | 频道信息表               |
| wm_fans_statistics         | 自媒体粉丝数据统计表     |
| wm_material                | 自媒体图文素材信息表     |
| wm_news                    | 自媒体图文内容信息表     |
| wm_news_material           | 自媒体图文引用素材信息表 |
| wm_news_statistics         | 自媒体图文数据统计表     |
| wm_user                    | 自媒体用户信息表         |

### 素材图片上传

思路如下：

1. 自媒体端发送上传请求至自媒体端网关
2. 网关将请求携带的 token 解析为用户存入 header 并路由至自媒体微服务端
3. 自媒体微服务端使用拦截器拦截请求从 header 中获取用户存入当前线程
4. 上传图片至 MinIO 并将 URL 保存至 DB

素材所对应的表是 wm_material

```sql
CREATE TABLE `wm_material` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `type` tinyint unsigned DEFAULT NULL COMMENT '素材类型\r\n            0 图片\r\n            1 视频',
  `is_collection` tinyint(1) DEFAULT NULL COMMENT '是否收藏',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文素材信息表'
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体图文素材信息表
 * </p>
 */
@Data
@Builder
@TableName("wm_material")
public class WmMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 图片地址
     */
    @TableField("url")
    private String url;

    /**
     * 素材类型
     * 0 图片
     * 1 视频
     */
    @TableField("type")
    private Short type;

    /**
     * 是否收藏
     */
    @TableField("is_collection")
    private Short isCollection;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

}
```

#### 解析用户存储

```java
package com.linxuan.wemedia.gateway.filter;

@Slf4j
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 2.判断是否是登录
        if (request.getURI().getPath().contains("/login")) {
            // 放行
            return chain.filter(exchange);
        }

        // 3.获取token
        String token = request.getHeaders().getFirst("token");

        // 4.判断token是否存在
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            // 是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 解析用户信息并存储请求头
            // 获取用户ID
            Object userId = claimsBody.get("id");
            // 将用户ID存入请求头
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            // 重置请求
            exchange.mutate().request(serverHttpRequest).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

#### 拦截器获取用户ID

拦截器将获取到的用户ID存入本地线程中，创建一个工具类存储

```java
package com.linxuan.utils.thread;

public class WmThreadLocalUtil {

    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     *
     * @param user
     */
    public static void setUser(WmUser user) {
        WM_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户
     *
     * @return
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
```

```java
package com.linxuan.wemedia.interceptor;

@Component
public class WmTokenInterceptor implements HandlerInterceptor {

    /**
     * 获取请求头中的userId信息并存入当前线程
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null) {
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.parseInt(userId));
            WmThreadLocalUtil.setUser(wmUser);
        }
        return true;
    }

    /**
     * 清理线程中数据
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        WmThreadLocalUtil.clear();
    }
}
```

```java
package com.linxuan.wemedia.config;

/**
 * 配置使拦截器生效，拦截所有的请求
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private WmTokenInterceptor requestUrlInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestUrlInterceptor).addPathPatterns("/**");
    }
}
```

#### 上传图片并保存DB

|          | **说明**                                     |
| -------- | -------------------------------------------- |
| 接口路径 | /api/v1/material/upload_picture              |
| 请求方式 | POST                                         |
| 参数     | MultipartFile（Springmvc指定的文件接收类型） |
| 响应结果 | ResponseResult                               |

```json
// 返回结果如下
{
    "host":null,
    "code":200,
    "errorMessage":"操作成功",
    "data":{
        "id":52,
        "userId":1102,
        "url":"http://192.168.88.129:9000/leadnews/2021/04/26/a73f5b60c0d84c32bfe175055aaaac40.jpg",
        "type":0,
        "isCollection":0,
        "createdTime":"2021-01-20T16:49:48.443+0000"
    }
}
```

自媒体微服务集成 file-starter

```xml
<artifactId>leadnews-wemedia</artifactId>    
<dependencies>
    <dependency>
        <groupId>com.linxuan.file</groupId>
        <artifactId>file-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

```yml
# nacos中配置minio信息
minio:
  accessKey: minio
  secretKey: minio123
  bucket: leadnews
  endpoint: http://192.168.88.129:9000
  readPath: http://192.168.88.129:9000
```

代码如下：

```java
package com.linxuan.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.model.wemedia.pojos.WmMaterial;

public interface WmMaterialMapper extends BaseMapper<WmMaterial> {
}
```

```java
package com.linxuan.wemedia.service;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);
}
```

```java
package com.linxuan.wemedia.service.impl;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 检查参数合法性
        if (multipartFile.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 上传至minio
        // 获取文件参数
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileUrl = null;
        try {
            fileUrl = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}", fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 保存至DB
        WmMaterial wmMaterial = WmMaterial.builder()
                .userId(WmThreadLocalUtil.getUser().getId())
                .url(fileUrl)
                .type((short) 0)
                .isCollection((short) 0)
                .createdTime(new Date())
                .build();
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/material/")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }
}
```

### 素材图片列表展示

|          | **说明**              |
| -------- | --------------------- |
| 接口路径 | /api/v1/material/list |
| 请求方式 | POST                  |
| 参数     | WmMaterialDto         |
| 响应结果 | ResponseResult        |

```json
// 请求成功响应结果
{
  "host":null,
  "code":200,
  "errorMessage":"操作成功",
  "data":[
    {
    "id":52,
      "userId":1102,
      "url":"http://192.168.88.129:9000/leadnews/2021/04/26/ec893175f18c4261af14df14b83cb25f.jpg",
      "type":0,
      "isCollection":0,
      "createdTime":"2021-01-20T16:49:48.000+0000"
    },
    ....
  ],
  "currentPage":1,
  "size":20,
  "total":0
}
```

```java
package com.linxuan.model.common.dtos;

@Data
@Slf4j
public class PageRequestDto {

    protected Integer size;
    protected Integer page;

    public void checkParam() {
        if (this.page == null || this.page < 0) {
            setPage(1);
        }
        if (this.size == null || this.size < 0 || this.size > 100) {
            setSize(10);
        }
    }
}
```

```java
package com.linxuan.model.wemedia.dtos;

@Data
public class WmMaterialDto extends PageRequestDto {
    // 0未收藏、1已收藏
    private Short isCollection;
}
```

列表查询需要用到分页插件，返回的也是分页信息，所以需要配置 MP 分页插件以及返回的 PageResponseResult

```java
package com.linxuan.wemedia;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.linxuan.wemedia.mapper")
public class WemediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
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
package com.linxuan.model.common.dtos;

public class PageResponseResult extends ResponseResult implements Serializable {
    private Integer currentPage;
    private Integer size;
    private Integer total;

    public PageResponseResult(Integer currentPage, Integer size, Integer total) {
        this.currentPage = currentPage;
        this.size = size;
        this.total = total;
    }

    public PageResponseResult() {

    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
```

编写代码

```java
package com.linxuan.wemedia.service;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 查询图片列表
     * @param wmMaterialDto
     * @return
     */
    public ResponseResult findList( WmMaterialDto wmMaterialDto);

}
```

```java
package com.linxuan.wemedia.service.impl;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    /**
     * 查询图片列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        // 检查参数合法性
        dto.checkParam();

        // 分页查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果是已收藏，那么添加该条件
        if (dto.getIsCollection() != null && dto.getIsCollection() == 1) {
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }
        // 按照用户查询
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());
        // 按照时间降序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        page = page(page, lambdaQueryWrapper);

        // 返回结果
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;

    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/material/")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 查询图片列表
     * @param wmMaterialDto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto wmMaterialDto) {
        return wmMaterialService.findList(wmMaterialDto);
    }
}
```

## 自媒体频道管理

| leadnews_wemedia库中表名称 | 自媒体库说明             |
| -------------------------- | ------------------------ |
| wm_channel                 | 频道信息表               |
| wm_fans_statistics         | 自媒体粉丝数据统计表     |
| wm_material                | 自媒体图文素材信息表     |
| wm_news                    | 自媒体图文内容信息表     |
| wm_news_material           | 自媒体图文引用素材信息表 |
| wm_news_statistics         | 自媒体图文数据统计表     |
| wm_user                    | 自媒体用户信息表         |

### 查询频道列表

```sql
CREATE TABLE `wm_channel` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频道名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '频道描述',
  `is_default` tinyint unsigned DEFAULT NULL COMMENT '是否默认频道',
  `status` tinyint unsigned DEFAULT NULL,
  `ord` tinyint unsigned DEFAULT NULL COMMENT '默认排序',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='频道信息表' |
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 频道信息表
 * </p>
 *
 */
@Data
@TableName("wm_channel")
public class WmChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道名称
     */
    @TableField("name")
    private String name;

    /**
     * 频道描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否默认频道
     * 1：默认     true
     * 0：非默认   false
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 是否启用
     * 1：启用   true
     * 0：禁用   false
     */
    @TableField("status")
    private Boolean status;

    /**
     * 默认排序
     */
    @TableField("ord")
    private Integer ord;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

}
```

|          | **说明**                 |
| -------- | ------------------------ |
| 接口路径 | /api/v1/channel/channels |
| 请求方式 | GET                      |
| 参数     | 无                       |
| 响应结果 | ResponseResult           |

```json
// 响应信息
{
  "host": "null",
  "code": 0,
  "errorMessage": "操作成功",
  "data": [
    {
      "id": 4,
      "name": "java",
      "description": "java",
      "isDefault": true,
      "status": false,
      "ord": 3,
      "createdTime": "2019-08-16T10:55:41.000+0000"
    },
    Object {  ... },
    Object {  ... }
  ]
}
```

```java
package com.linxuan.wemedia.mapper;

public interface WmChannelMapper extends BaseMapper<WmChannel> {
}
```

```java
package com.linxuan.wemedia.service;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询文章列表信息
     * @return
     */
    public ResponseResult findAll();
}
```

```java
package com.linxuan.wemedia.service.impl;

@Slf4j
@Service
@Transactional
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    /**
     * 查询文章列表信息
     *
     * @return
     */
    @Override
    public ResponseResult findAll() {
        return ResponseResult.okResult(list());
    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel/")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    /**
     * 查询文章频道列表信息
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }
}
```

### 查询文章列表

```sql
CREATE TABLE `wm_news` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `title` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图文内容',
  `type` tinyint unsigned DEFAULT NULL COMMENT '文章布局\r\n            0 无图文章\r\n            1 单图文章\r\n            3 多图文章',
  `channel_id` int unsigned DEFAULT NULL COMMENT '图文频道ID',
  `labels` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `submited_time` datetime DEFAULT NULL COMMENT '提交时间',
  `status` tinyint unsigned DEFAULT NULL COMMENT '当前状态\r\n            0 草稿\r\n            1 提交（待审核）\r\n            2 审核失败\r\n            3 人工审核\r\n            4 人工审核通过\r\n            8 审核通过（待发布）\r\n            9 已发布',
  `publish_time` datetime DEFAULT NULL COMMENT '定时发布时间，不定时则为空',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝理由',
  `article_id` bigint unsigned DEFAULT NULL COMMENT '发布库文章ID',
  `images` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '//图片 用逗号分隔',
  `enable` tinyint unsigned DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文内容信息表'
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体图文内容信息表
 * </p>
 */
@Data
@TableName("wm_news")
public class WmNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图文内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章布局
     * 0 无图文章
     * 1 单图文章
     * 3 多图文章
     */
    @TableField("type")
    private Short type;

    /**
     * 图文频道ID
     */
    @TableField("channel_id")
    private Integer channelId;

    // 文章标签
    @TableField("labels")
    private String labels;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 提交时间
     */
    @TableField("submited_time")
    private Date submitedTime;

    /**
     * 当前状态
     * 0 草稿
     * 1 提交（待审核）
     * 2 审核失败
     * 3 人工审核
     * 4 人工审核通过
     * 8 审核通过（待发布）
     * 9 已发布
     */
    @TableField("status")
    private Short status;

    /**
     * 定时发布时间，不定时则为空
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 拒绝理由
     */
    @TableField("reason")
    private String reason;

    /**
     * 发布库文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * //图片用逗号分隔
     */
    @TableField("images")
    private String images;

    // 是否上架
    @TableField("enable")
    private Short enable;

    // 状态枚举类
    @Alias("WmNewsStatus")
    public enum Status {
        NORMAL((short) 0), SUBMIT((short) 1), FAIL((short) 2), ADMIN_AUTH((short) 3), ADMIN_SUCCESS((short) 4), SUCCESS((short) 8), PUBLISHED((short) 9);
        short code;

        Status(short code) {
            this.code = code;
        }

        public short getCode() {
            return this.code;
        }
    }
}
```

|          | **说明**          |
| -------- | ----------------- |
| 接口路径 | /api/v1/news/list |
| 请求方式 | POST              |
| 参数     | WmNewsPageReqDto  |
| 响应结果 | ResponseResult    |

```java
package com.linxuan.model.common.dtos;

@Data
@Slf4j
public class PageRequestDto {

    protected Integer size;
    protected Integer page;

    public void checkParam() {
        if (this.page == null || this.page < 0) {
            setPage(1);
        }
        if (this.size == null || this.size < 0 || this.size > 100) {
            setSize(10);
        }
    }
}

```

```java
package com.linxuan.model.wemedia.dtos;

@Data
public class WmNewsPageReqDto extends PageRequestDto {

    /**
     * 状态
     */
    private Short status;
    /**
     * 开始时间
     */
    private Date beginPubDate;
    /**
     * 结束时间
     */
    private Date endPubDate;
    /**
     * 所属频道ID
     */
    private Integer channelId;
    /**
     * 关键字
     */
    private String keyword;
}
```

```json
// 返回信息
{
  "host": "null",
  "code": 0,
  "errorMessage": "操作成功",
  "data": [
    Object { ... },
    Object { ... },
    Object { ... }
    
  ],
  "currentPage":1,
  "size":10,
  "total":21
}
```

```java
package com.linxuan.wemedia.mapper;

public interface WmNewsMapper extends BaseMapper<WmNews> {
}
```

```java
package com.linxuan.wemedia.service;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 根据条件查询自媒体端文章列表
     * @param dto
     * @return
     */
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto);
}

```

```java
package com.linxuan.wemedia.service.impl;

@Slf4j
@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        // 校验参数合法性
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 构造查询条件
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getCreatedTime, 
                                       dto.getBeginPubDate(), dto.getEndPubDate());
        }
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        if (dto.getKeyword() != null) {
            lambdaQueryWrapper.eq(WmNews::getTitle, dto.getKeyword());
        }
        // 查询当前用户发布的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());
        // 按照时间倒叙
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        // 分页查询
        page = page(page, lambdaQueryWrapper);

        // 返回查询信息
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/news/")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.findAll(dto);
    }
}
```

### 文章发布及修改

| 文章发布及修改使用leadnews_wemedia库中表 | 自媒体库说明             |
| ---------------------------------------- | ------------------------ |
| wm_material                              | 自媒体图文素材信息表     |
| wm_news                                  | 自媒体图文内容信息表     |
| wm_news_material                         | 自媒体图文引用素材信息表 |

#### 表结构分析

```sql
CREATE TABLE `wm_news` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `title` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图文内容',
  `type` tinyint unsigned DEFAULT NULL COMMENT '文章布局\r\n            0 无图文章\r\n            1 单图文章\r\n            3 多图文章',
  `channel_id` int unsigned DEFAULT NULL COMMENT '图文频道ID',
  `labels` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `submited_time` datetime DEFAULT NULL COMMENT '提交时间',
  `status` tinyint unsigned DEFAULT NULL COMMENT '当前状态\r\n            0 草稿\r\n            1 提交（待审核）\r\n            2 审核失败\r\n            3 人工审核\r\n            4 人工审核通过\r\n            8 审核通过（待发布）\r\n            9 已发布',
  `publish_time` datetime DEFAULT NULL COMMENT '定时发布时间，不定时则为空',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝理由',
  `article_id` bigint unsigned DEFAULT NULL COMMENT '发布库文章ID',
  `images` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '//图片 用逗号分隔',
  `enable` tinyint unsigned DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文内容信息表';
```

```sql
CREATE TABLE `wm_material` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `type` tinyint unsigned DEFAULT NULL COMMENT '素材类型\r\n            0 图片\r\n            1 视频',
  `is_collection` tinyint(1) DEFAULT NULL COMMENT '是否收藏',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文素材信息表';
```

```sql
CREATE TABLE `wm_news_material` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` int unsigned DEFAULT NULL COMMENT '素材ID',
  `news_id` int unsigned DEFAULT NULL COMMENT '图文ID',
  `type` tinyint unsigned DEFAULT NULL COMMENT '引用类型\r\n            0 内容引用\r\n            1 主图引用',
  `ord` tinyint unsigned DEFAULT NULL COMMENT '引用排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文引用素材信息表';
```

自媒体图文内容信息表 wm_news 与自媒体图文素材信息表  wm_material 是多对多的关系，一篇图文内容可以引用多个图文素材、一个图文素材也可以被多篇图文内容引用。因此创建 wm_news_material 表来表示自媒体图文引用素材信息表。

此外还有一个好处：当被引用素材要删除的时候会先查询 wm_news_material 看看是否存在引用数据，如果存在那么自然不能被删除。

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体图文内容信息表
 * </p>
 */
@Data
@TableName("wm_news")
public class WmNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图文内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章布局
     * 0 无图文章
     * 1 单图文章
     * 3 多图文章
     */
    @TableField("type")
    private Short type;

    /**
     * 图文频道ID
     */
    @TableField("channel_id")
    private Integer channelId;

    @TableField("labels")
    private String labels;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 提交时间
     */
    @TableField("submited_time")
    private Date submitedTime;

    /**
     * 当前状态
     * 0 草稿
     * 1 提交（待审核）
     * 2 审核失败
     * 3 人工审核
     * 4 人工审核通过
     * 8 审核通过（待发布）
     * 9 已发布
     */
    @TableField("status")
    private Short status;

    /**
     * 定时发布时间，不定时则为空
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 拒绝理由
     */
    @TableField("reason")
    private String reason;

    /**
     * 发布库文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * //图片用逗号分隔
     */
    @TableField("images")
    private String images;

    @TableField("enable")
    private Short enable;

    // 状态枚举类
    @Alias("WmNewsStatus")
    public enum Status {
        NORMAL((short) 0), SUBMIT((short) 1), FAIL((short) 2), ADMIN_AUTH((short) 3), ADMIN_SUCCESS((short) 4), SUCCESS((short) 8), PUBLISHED((short) 9);
        short code;

        Status(short code) {
            this.code = code;
        }

        public short getCode() {
            return this.code;
        }
    }
}
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体图文素材信息表
 * </p>
 */
@Data
@Builder
@TableName("wm_material")
public class WmMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 图片地址
     */
    @TableField("url")
    private String url;

    /**
     * 素材类型
     * 0 图片
     * 1 视频
     */
    @TableField("type")
    private Short type;

    /**
     * 是否收藏
     */
    @TableField("is_collection")
    private Short isCollection;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

}
```

```java
package com.linxuan.model.wemedia.pojos;

/**
 * <p>
 * 自媒体图文引用素材信息表
 * </p>
 */
@Data
@TableName("wm_news_material")
public class WmNewsMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 素材ID
     */
    @TableField("material_id")
    private Integer materialId;

    /**
     * 图文ID
     */
    @TableField("news_id")
    private Integer newsId;

    /**
     * 引用类型
     * 0 内容引用
     * 1 主图引用
     */
    @TableField("type")
    private Short type;

    /**
     * 引用排序
     */
    @TableField("ord")
    private Short ord;

}
```

#### 思路分析

该接口要实现的功能为提交文章、存入草稿、修改文章（存入草稿或者提交文章之后可以进行编辑，此时再发送请求就是修改文章）。

1. 前端提交发布或保存草稿
2. 判断请求中是否包含了文章 id
   1. 不包含id，则为新增文章操作
      1. 执行新增文章操作
      2. 判断是否为草稿
         1. 是草稿，结束操作
         2. 不是草稿
            1. 关联文章内容图片素材与图文的关系
            2. 关联文章封面图片素材与图文的关系。如果封面选择的是自动，那么按照规则匹配封面图片。
   2. 包含了 id，则为修改请求
      1. 删除该文章与素材的所有关系
      2. 执行修改操作
      3. 判断是否为草稿
         1. 是草稿，结束操作
         2. 不是草稿
            1. 关联文章内容图片与图文的关系
            2. 关联文章封面图片与图文的关系。如果封面选择的是自动，那么按照规则匹配封面图片。

#### 接口定义

|          | **说明**               |
| -------- | ---------------------- |
| 接口路径 | /api/v1/channel/submit |
| 请求方式 | POST                   |
| 参数     | WmNewsDto              |
| 响应结果 | ResponseResult         |

```java
package com.linxuan.model.wemedia.dtos;

@Data
public class WmNewsDto {

    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 频道id
     */
    private Integer channelId;

    /**
     * 标签
     */
    private String labels;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面类型  0 无图 1 单图 3 多图 -1 自动
     */
    private Short type;

    /**
     * 提交时间
     */
    private Date submitedTime;

    /**
     * 状态 提交为1  草稿为0
     */
    private Short status;

    /**
     * 封面图片列表 多张图以逗号隔开
     */
    private List<String> images;
}
```

```json
// 前端传送请求体JSON数据格式
{
    "title":"头条项目背景",
    "type":"1", // 这个0是无图  1是单图  3是多图  -1是自动
    "labels":"背景",
    "publishTime":"2020-03-14T11:35:49.000Z",
    "channelId":1,
    // 封面图片列表，可以与文章内容里面的图片重复、也可以不重复、也可以不上传或者是自动获取
    "images":[
        "http://192.168.88.129/group1/M00/00/00/wKjIgl5swbGATaSAAAEPfZfx6Iw790.png"
    ],
    "status":1,
    // 文章内容，不仅有文字而且有图片加以装饰。如果设置封面图片类型为-1自动，那么会从文章内容中获取图片至封面
    "content":"[
    {
        "type":"text",
        "value":"随着智能手机的普及，人们更加习惯于通过手机来看新闻。由于生活节奏的加快，很多人只能利用碎片时间来获取信息，因此，对于移动资讯客户端的需求也越来越高。头条项目正是在这样背景下开发出来。头条项目采用当下火热的微服务+大数据技术架构实现。本项目主要着手于获取最新最热新闻资讯，通过大数据分析用户喜好精确推送咨询新闻"
    },
    {
        "type":"image",
        "value":"http://192.168.88.129/group1/M00/00/00/wKjIgl5swbGATaSAAAEPfZfx6Iw790.png"
    }
]"
}
```

```json
// ResponseResult返回前端数据格式
{
    "code":501,
    "errorMessage":"参数失效"
}

{
    "code":200,
    "errorMessage":"操作成功"
}

{
    "code":501,
    "errorMessage":"素材引用失效"
}
```

#### 代码编写

首先定义接口，先在控制层创建方法

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/news/")
public class WmNewsController {

    /**
     * 文章发布及修改
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {
        return null;
    }
}
```

```java
package com.linxuan.wemedia.mapper;

public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 批量保存文章与素材的关联关系
     * MP里面并没有该方法，所以自己实现
     * @param materialIds 所有素材ID
     * @param newsId 文章ID
     * @param type 0内容引用、1主图引用/封面引用
     */
    void saveRelations(@Param("materialIds") List<Integer> materialIds,
                       @Param("newsId") Integer newsId,
                       @Param("type") Short type);
}

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- resources/mapper/WmNewsMaterialMapper.xml配置 -->
<!-- 正常而言是要放在同名目录下的也就是resources/com/linxuan/wemedia/mapper/WmNewsMaterialMapper.xml，但是已经在Nacos中配置了查找映射文件路径 -->
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linxuan.wemedia.mapper.WmNewsMaterialMapper">

    <!-- void saveRelations(@Param("materialIds") List<Integer> materialIds,
                       @Param("newsId") Integer newsId,
                       @Param("type") Short type); -->
    <insert id="saveRelations">
        insert into wm_news_material (material_id, news_id, type, ord)
        values
        <foreach collection="materialIds" index="ord" item="mid" separator=",">
            (#{mid}, #{newsId}, #{type}, #{ord})
        </foreach>
    </insert>
</mapper>
```

```java
package com.linxuan.common.constants;

public class WemediaConstants {
    // 收藏
    public static final Short COLLECT_MATERIAL = 1;
    // 取消收藏
    public static final Short CANCEL_COLLECT_MATERIAL = 0;

    // 类型
    public static final String WM_NEWS_TYPE = "type";
    // 文本
    public static final String WM_NEWS_TYPE_TEXT = "text";
    // 图片
    public static final String WM_NEWS_TYPE_IMAGE = "image";

    // 文章封面类型 0无图 1单图 3多图 -1自动
    public static final Short WM_NEWS_NONE_IMAGE = 0;
    public static final Short WM_NEWS_SINGLE_IMAGE = 1;
    public static final Short WM_NEWS_MANY_IMAGE = 3;
    public static final Short WM_NEWS_TYPE_AUTO = -1;

    // 素材图片引用类型 0内容引用、1封面引用
    public static final Short WM_CONTENT_REFERENCE = 0;
    public static final Short WM_COVER_REFERENCE = 1;
}
```

```java
package com.linxuan.model.common.enums;

@Getter
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
    NEED_ADMIND(3001,"需要管理员权限"),

    // 添加该枚举
    // 自媒体文章错误 3501~3600
    MATERIASL_REFERENCE_FAIL(3501, "素材引用失效");


    final int code;
    final String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
```

```java
package com.linxuan.wemedia.service;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 发布文章或者保存草稿
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(@RequestBody WmNewsDto dto);
}
```

```java
package com.linxuan.wemedia.service.impl;

@Slf4j
@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 发布文章或者保存草稿
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {
        // 校验参数合法性
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 保存或修改文章
        // 设置参数保存DB
        WmNews wmNews = new WmNews();
        // 先将大部分参数由dto拷贝至wmNews对象，只有属性名称和类型相同才会拷贝
        BeanUtils.copyProperties(dto, wmNews);
        // 设置封面图片列表引用
        if (dto.getImages() != null) {
            String coverImages = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(coverImages);
        }
        // 数据库里封面图片类型type字段是unsigned无符号类型，没有-1存在，前端传过来-1将其改为null
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        // 保存或修改文章
        saveOrUpdateWmNews(wmNews);

        // 判断是否为草稿，如果是草稿结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 不是草稿，保存图文与文章内容图片素材的关系
        // 获取到文章内容图片素材列表
        List<String> imageContentUrls = new ArrayList<>();
        List<Map> maps = JSON.parseArray(dto.getContent(), Map.class);
        for (Map map : maps) {
            if (map.get("type").equals(WemediaConstants.WM_NEWS_TYPE_IMAGE)) {
                imageContentUrls.add(map.get("value").toString());
            }
        }
        // 保存关系
        saveRelativeInfoForContent(imageContentUrls, wmNews.getId());

        // 不是草稿，保存文章封面图片与图文的关系，如果当前布局是自动，需要在内容图片引用中匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, imageContentUrls);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    public void saveOrUpdateWmNews(WmNews wmNews) {
        // 补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setCreatedTime(new Date());
        wmNews.setEnable((short) 1);

        // 如果包含ID那么就是修改请求，否则是新增请求
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            // 删除文章与素材所有关系
            wmNewsMaterialMapper.delete(new LambdaQueryWrapper<WmNewsMaterial>()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            // 修改操作
            updateById(wmNews);
        }
    }

    /**
     * 处理图文与图片素材的关系
     *
     * @param imageContentUrls 图文内容中引用的图片素材列表
     * @param newsId           图文ID
     */
    private void saveRelativeInfoForContent(List<String> imageContentUrls, Integer newsId) {
        saveRelativeInfo(imageContentUrls, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存封面图片与素材的关系
     * 如果封面图片类型为自动，那么根据图文内容中引用的图片数量设置封面布局
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * @param dto              前端传过来的图文数据，用来获取封面图片素材
     * @param wmNews           设置保存数据库中数据信息，这里主要设置type文章布局
     * @param imageContentUrls 图文内容引用的素材图片列表
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> imageContentUrls) {
        List<String> images = dto.getImages();
        // 如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            // 内容引用素材图片列表大于3，设置封面布局为3图，并切割前三张图片
            if (imageContentUrls.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = imageContentUrls.stream().limit(3).collect(Collectors.toList());
            } else if (imageContentUrls.size() >= 1) {
                // 单图布局
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = imageContentUrls.stream().limit(1).collect(Collectors.toList());
            } else {
                // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            // 上面对wmNews对象的type属性修改了，之前已经将该对象存入DB，所以需要修改一下
            if (images != null && images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }
        // 保存数据
        saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
    }


    /**
     * 保存图文与图片素材关系到数据库wm_news_material表中
     *
     * @param materials 素材图片
     * @param newsId    图文ID
     * @param type      素材图片引用类型 0内容引用、1封面引用
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()) {
            // 根据素材图片列表URL查询ID，有多少图片就要有多少ID，否则抛异常
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(new LambdaQueryWrapper<WmMaterial>().in(WmMaterial::getUrl, materials));
            if (dbMaterials == null
                    || dbMaterials.isEmpty()
                    || dbMaterials.size() != materials.size()) {
                // 手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            // 获取素材列表中素材ID列表
            List<Integer> idMaterials = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
            // 保存
            wmNewsMaterialMapper.saveRelations(idMaterials, newsId, type);
        }
    }
}
```

```java
package com.linxuan.wemedia.controller.v1;

@Slf4j
@RestController
@RequestMapping("/api/v1/news/")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 文章发布及修改
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {

        return wmNewsService.submitNews(dto);
    }
}
```







