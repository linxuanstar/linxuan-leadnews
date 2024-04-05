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

| leadnews_article库中表名称 | 文章库说明                   |
| -------------------------- | ---------------------------- |
| ap_article                 | 文章信息表，存储已发布的文章 |
| ap_article_config          | APP 已发布文章配置表         |
| ap_article_content         | APP 已发布文章内容表         |
| ap_author                  | APP 文章作者信息表           |
| ap_collection              | APP 收藏信息表               |

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
