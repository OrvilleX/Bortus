# Bortus数据管理平台

[![GitHub stars](https://img.shields.io/github/stars/elunez/eladmin.svg?style=social&label=Stars)](https://github.com/OrvilleX/Bortus)
[![GitHub forks](https://img.shields.io/github/forks/elunez/eladmin.svg?style=social&label=Fork)](https://github.com/OrvilleX/Bortus)

## 项目简介  

中文名柏图斯，提供企业级管理平台，便于管理旗下的相关大数据产品功能。  

### 主要特性  

- 使用最新技术栈，社区资源丰富。
- 高效率开发，代码生成器可一键生成前后端代码
- 支持数据字典，可方便地对一些状态进行管理
- 支持接口限流，避免恶意请求导致服务层压力过大
- 支持接口级别的功能权限与数据权限，可自定义操作
- 自定义权限注解与匿名接口注解，可快速对接口拦截与放行
- 对一些常用地前端组件封装：表格数据请求、数据字典等
- 前后端统一异常拦截处理，统一输出异常，避免繁琐的判断
- 支持在线用户管理与服务器性能监控，支持限制单用户登录
- 支持运维管理，可方便地对远程服务器的应用进行部署与管理

###  系统功能
- 用户管理：提供用户的相关配置，新增用户后，默认密码为123456
- 角色管理：对权限与菜单进行分配，可根据部门设置角色的数据权限
- 菜单管理：已实现菜单动态路由，后端可配置化，支持多级菜单
- 部门管理：可配置系统组织架构，树形表格展示
- 岗位管理：配置各个部门的职位
- 字典管理：可维护常用一些固定的数据，如：状态，性别等
- 系统日志：记录用户操作日志与异常日志，方便开发人员定位拍错
- SQL监控：采用druid 监控数据库访问性能，默认用户名admin，密码123456
- 定时任务：整合Quartz做定时任务，加入任务日志，任务运行情况一目了然
- 代码生成：高灵活度生成前后端代码，减少大量重复的工作任务
- 邮件工具：配合富文本，发送html格式的邮件
- 免费图床：使用sm.ms图床，用作公共图片上传使用，该图床不怎么稳定，不太建议使用
- 七牛云存储：可同步七牛云存储的数据到系统，无需登录七牛云直接操作云数据
- 支付宝支付：整合了支付宝支付并且提供了测试账号，可自行测试
- 服务监控：监控服务器的负载情况
- 运维管理：一键部署你的应用

### 项目结构
项目采用按功能分模块的开发方式，结构如下

- `eladmin-common` 为系统的公共模块，各种工具类，公共配置存在该模块

- `eladmin-system` 为系统核心模块也是项目入口模块，也是最终需要打包部署的模块

- `eladmin-logging` 为系统的日志模块，其他模块如果需要记录日志需要引入该模块

- `eladmin-tools` 为第三方工具模块，包含：图床、邮件、云存储、本地存储、支付宝

- `eladmin-generator` 为系统的代码生成模块，代码生成的模板在 system 模块中

#### 详细结构

```
- eladmin-common 公共模块
    - annotation 为系统自定义注解
    - aspect 自定义注解的切面
    - base 提供了Entity、DTO基类和mapstruct的通用mapper
    - config 自定义权限实现、redis配置、swagger配置、Rsa配置等
    - exception 项目统一异常的处理
    - utils 系统通用工具类
- eladmin-system 系统核心模块（系统启动入口）
	- config 配置跨域与静态资源，与数据权限
	    - thread 线程池相关
	- modules 系统相关模块(登录授权、系统监控、定时任务、运维管理等)
- eladmin-logging 系统日志模块
- eladmin-tools 系统第三方工具模块
- eladmin-generator 系统代码生成模块
```

## 如何开发  

### 仓储开发  

* 使用自定义结果  

由于仓储层本身使用了JPA框架，在实际研发过程中往往需要返回非Entity对象，为了满足这类需求用户可以采用自定义Pojo的方式，然后通过在`Query`中添加对应代码即可，比如下面这种方式：  

```java
@Query(value = "select new com.Vo.CustomModel(a.bookpkid,a.bookcode,a.bookname,b.authorname) " +
        "FROM BookInfo a LEFT JOIN AuthorInfo b ON a.authorid=b.authorid WHERE a.bookpkid=:pkid")
List<CustomModel> selectModelByPkid(@Param("pkid") BigDecimal pkid);
```

其中需要注意模型必须采用全路径的格式，否则会无法找到具体的类，当然这类方式存在一个致命的缺点就是该类发生变化或者需要修改则无法及时修正，为此建议读者使用`EntityUtils`工具类提供的方法进行转换，而仓储层采用返回`Object[]`的方式接收单个对象，或者`List<Object[]>`接收多个自定义对象，并采用`T caseEntity(Object[] entity, Class<T> clazz)`进行转换即可。  


