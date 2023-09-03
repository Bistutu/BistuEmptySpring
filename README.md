# BISTU空教室查询项目——后端

> 项目后端采用 Java 框架 Spring Boot 编写，历经 4 次重构，目前并不完善。

## 简介

该项目是用于查询 BISTU 空闲教室的Spring后端服务，该服务提供了简单易用的API，方便前端或其他服务进行调用，代码具体操作流程如下：

<img src="https://oss.thinkstu.com/typora/202309031607060.png?x-oss-process=style/optimize" alt="image-20230903160734038" width="33%" />

## 环境要求

- Java 17 或更高版本
- Spring Boot 3.0.6 或更高版本

## 快速开始

> 本地运行

1. 克隆该仓库

   ```shell
   git clone https://github.com/Bistutu/BistuEmptySpring.git
   ```
2. 修改 `application.yaml` 配置文件

   - 设置[教务网](https://jwxt.bistu.edu.cn/jwapp/sys/emaphome/portal/index.do)登录账号与密码（需经过 Base64 编码，防止明文直接泄露）
   - 设置空教室数据在本机的缓存路径

   ```yaml
   # 教务网账号密码
   username: base64编码后的账号
   password: base64编码后的密码
   
   # 设置空教室数据缓存路径（默认当前项目的 /data 目录）
   saved_path: "./data/"
   ```

   <img src="https://oss.thinkstu.com/typora/202309031606222.png?x-oss-process=style/optimize" alt="image-20230903160608131" width="50%" />

3. 直接运行项目

### 项目目录结构

> 代码遵循 Spring Boot 通用的项目目录结构

```shell
├── campus	校区逻辑代码
├── config	网络框架配置信息
├── controller	接口
├── entity	实体类
├── helper	一些加解密与编解码工具
├── process	教务网登录模块
├── service	服务方法
├── timer	定时器，用于定时向教务网获取空教室数据
└── utils	工具
```

## 开发者

ThinkStu

## 许可证

MIT License

























