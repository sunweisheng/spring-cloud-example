# 通过总线机制实现自动刷新客户端配置

## 方案示意图

![Alt text](http://static.bluersw.com/images/spring-cloud-config-server/spring-cloud-config-server-08.png)  

利用Git服务的webhook通知功能，在每次更新配置之后，Git服务器会用POST方式调用配置中心的bus/actuator/refresh接口，配置中心的总线服务会将此事件广播给加入总线的所有客户端，客户端收到事件后会从新读取配置中心的内容。

## 增加POM依赖

配置中心的服务端（spring-cloud-config-server）和客户端（spring-cloud-config-client）都加入Spring Cloud Bus引用包：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

## 启动Rabbitmq

```shell
docker pull rabbitmq

docker run -d --hostname my-rabbit --name rabbit -p 5672:5672  rabbitmq:3

```

## 修改配置信息

配置中心的服务端（spring-cloud-config-server）和客户端（spring-cloud-config-client）都需要修改配置文件的内容：  
spring-cloud-config-server项目的application.properties增加：

```text
#显示的暴露接入点
management.endpoints.web.exposure.include=refresh,health,info
## 开启消息跟踪
spring.cloud.bus.trace.enabled=true
spring.rabbitmq.host=127.0.0.1
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
#关闭安全验证
management.security.enabled=false
```

spring-cloud-config-client项目的application.properties增加：

```text
## 开启消息跟踪
spring.cloud.bus.trace.enabled=true
spring.rabbitmq.host=127.0.0.1
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
#关闭安全验证
management.security.enabled=false
```

spring-cloud-config-client项目的bootstrap.properties增加(否则会报错：A component required a bean named 'configServerRetryInterceptor' that could)：

```text
spring.cloud.config.fail-fast=true
```

## 测试半自动刷新自动

访问客户端程序127.0.0.1:9006/ConfigTest，得到结果Test-5，我们更新Git参考将配置内容改为Test-6，查看配置中心127.0.0.1:9004/ConfigDepot/Test，内容已经改为Test-6,但刷新客户端程序127.0.0.1:9006/ConfigTest，值没有改变，执行以下脚本模式Git的Webhook功能：

```shell
curl -X POST http://127.0.0.1:9004/bus/actuat/refresh
```

执行成功后再刷新客户端程序127.0.0.1:9006/ConfigTest，这时配置内容已经成功改成了Test-6，总线事件通知客户端刷新配置成功。  
