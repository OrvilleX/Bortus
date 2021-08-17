# Spring Boot 技术  

### 1. CommandLineRunner  

如果需要在项目运行后执行相关的初始化工作，可以新建一个类并实现`CommandLineRunner`
接口，通过其中的`run`方法实现相关的工作。如果有多个对象需要注意不能执行阻塞类的代
码，将会导致后续的执行无法继续执行。  

```java
@Component
@Order(value=1)
public class MyStartupRunner2 implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动执行 111111 <<<<<<<<<<<<<");
    }
}
```

### 2. ElasticSearch仓储  

考虑到特殊的检索场景，仅仅依靠传统数据库无法满足这类场景。为此我们需要使用Elastic
Search来满足。为了使用这种存储我们需要安装对应的依赖在`pom.xml`中。  

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```  

安装完具体依赖后，我们需要编写对应的模型。  

```java
@Getter
@Setter
@Document(indexName = "customer", type = "customer", shards = 1, replicas = 0, refreshInterval = "-1")
public class Customer {
	@Id
	private String id;

	private String userName;

	private String address;

	private int age;
}
```  

完成模型的编写后，我们就需要编写对应的仓储以实现数据的读取。  

```java
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {
	public List<Customer> findByAddress(String address);
	public Customer findByUserName(String userName);
	public int  deleteByUserName(String userName);
	public Page<Customer> findByAddress(String address, Pageable pageable);
}
```  

虽然仓储能够满足我们大多数的需求，但是有时候还是需要更高级的自定义方式来满足
我们特定场景的需求，比如以下方式。  

```java
Pageable pageable = new PageRequest(pageNumber, pageSize);
// Function Score Query
FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
        .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("cityname", searchContent)),
                ScoreFunctionBuilders.weightFactorFunction(1000))
        .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("description", searchContent)),
                ScoreFunctionBuilders.weightFactorFunction(100));

// 创建搜索 DSL 查询
SearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withPageable(pageable)
        .withQuery(functionScoreQueryBuilder).build();
logger.info("\n searchCity(): searchContent [" + searchContent + "] \n DSL  = \n " + searchQuery.getQuery().toString());
Page<Customer> searchPageResults = customerRepository.search(searchQuery);
return searchPageResults.getContent();
```

最后就是填写相关的配置文件项。  

```yml
spring:
  data:
    elasticsearch:
      cluster-name: es-mongodb
      cluster-nodes: 192.168.0.53:9300
```  

### 3. JPA自动生成语句  

如果需要根据模型自动生成数据库语句仅需要将`ddl-auto`配置为其他非`none`值即可，
比如下方方式：   

```yaml
spring:
  profiles:
    active: dev
  jpa:
    hibernate:
      ddl-auto: update
```

其中可选值如下：
* create：启动时删除数据库中的表并重建;  
* create-drop: 启动时删除数据库中的表并重建，退出删除数据库表;  
* update：启动时如果表格式不一致则更新，保留数据;  
* validate: 启动时校验表格式是否一致，不一致则报错;  

如果使用OnToMany，子对象指向父对象的字段需要可为空，因为JPA是先新增子数据然后更新指向的主键。本框架已自带
软删除机制，要求凡继承自`BaseEntity`的模型仓储必须实现`BaseRepository`接口，同时数据库字典中需要增加如下
所示的字段。  

```sql
`is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '软删除',
```  

完成以上工作后可通过扩展的`void logicDelete(ID id)`方法删除需要删除的数据，对于原生编写的SQL需要用户自行
增加对应判断字段`where logicDelete = 0`以排除已软删除的数据。  

### 4. netty-socketio使用  

从当前B/S的实际需求出发，使用者对于实时性要求越来越高。而采用以往的轮询或者长连接的技术对于浏览器的压力较大且
实时度减低，为了彻底解决这类需求，本框架将采用netty-socketio框架为其提供技术支撑。下面我们将介绍如何使用。  

首先服务相关配置以及原生接口的注入已经实现，具体可以参考`config/socketio`文件夹下，对于需要使用其高级特性的用户
则可以直接引用其实现类进行操作，而其他常规情况下建议使用本框架已封装好的服务已经访问，当然读者也可以对此进行二次开
发以满足个性化的开发需求，其实现在`service`文件夹下，该文件将统一放置所有通用服务的实现。当前服务提供了对应的方法
如下。   

```java
public interface SocketioService {
    /**
     * 推送的事件
     */
    public static final String PUSH_EVENT = "push_event";
    
    /**
     * 推送信息
     */
    void pushMessageToUser(PushMessage pushMessage);
}
```  

### 5. Hystrix熔断使用  

现如今的系统架构也不同于以往的单体架构，而是朝着多服务的架构，甚至是微服务架构。这么拆分后服务之间的就必然存在远程
调用问题，而在远程调用的过程中我们往往无法保障对方服务的可靠性，所以产生了各种自定义的错误代码以提供记录。但是这仅
仅是利用`try...catch...`手段所能达成的效果。其本身也是一种高资源消耗，为了保证服务能够在设定的阈值后自动熔断以减
少对应用的影响，本框架引入了`hystrix`框架以提供支撑。  

首先需要在引用对应的依赖：  

```xml
<!-- hystrix 熔断插件 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
<!-- hystrix 熔断可视化依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```  

完成上述的引用后，我们还需要启用对应的服务并进行配置。首先打开`main`函数并增加`@EnableHystrix`注解以启用该插件。随
后我们就可以设定全局的规则，打开`application.yml`文件并添加如下内容：  

```yaml
# Hystrix settings
hystrix:
  command:
    default:
      execution:
        isolation:
          strategy: THREAD
          thread:
            # 线程超时15秒,调用Fallback方法
            timeoutInMilliseconds: 15000
      metrics:
        rollingStats:
          timeInMilliseconds: 15000
      circuitBreaker:
        # 10秒内出现3个以上请求(已临近阀值),并且出错率在50%以上,开启断路器.断开服务,调用Fallback方法
        requestVolumeThreshold: 3
        sleepWindowInMilliseconds: 10000

# actuator配置
management:
  endpoints:
    web:
      exposure:
        include: hystrix.stream
```  

完成以上的配置后，我们就可以使用其来帮助我们进行服务的熔断降级，下面我们将以一个Rest接口为例进行介绍，当然这里是基于全局默认
的策略进行配置。  

```java
  @GetMapping
  @PreAuthorize("@x.check()")
  @HystrixCommand(fallbackMethod = "queryError")
  public ResponseEntity<Object> query(LogQueryCriteria criteria, Pageable pageable) throws IOException {
      criteria.setLogType("INFO");
      throw new IOException();
      //return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
  }
  
  public ResponseEntity<Object> queryError(LogQueryCriteria criteria, Pageable pageable) {
      criteria.setLogType("INFO");
      return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
  }
```  

这里可以看到我们在需要提供熔断支持的接口上增加了`HystrixCommand`注解，并通过其`fallbackMethod`指定了当出现熔断后需要调用
的服务方法，这里为了便于进行演示，直接进行了异常抛出。读者可以自行访问即可发现当然出现错误后将自动调用`queryError`方法。当
然读者也可以自行设定对应的规则，而不许遵从全局的规则，具体的使用方式如下。  

```java
@HystrixCommand(fallbackMethod = "onError",
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")},
        threadPoolProperties = {
                @HystrixProperty(name = "coreSize", value = "5"),
                @HystrixProperty(name = "maximumSize", value = "5"),
                @HystrixProperty(name = "maxQueueSize", value = "10")
        })
```  

如果读者希望了解其参数的具体说明与使用可以[参考本文档](https://github.com/Netflix/Hystrix/wiki/Configuration)  

# 单元测试  

### 1. BigDecimal类型测试  

如果使用`Assert.assertEquals`对`BigDecimal`进行比较时将会发现虽然值是
完全一样的但是依然比对失败，针对这种特殊类型我们需要利用其对象本身的`compareTo`
方法来完成比较，并且最终的值只有等于0才代表相同，所以最终的比较方式应该如下：  

```java
Assert.assertEquals(item.getPerConsumption().compareTo(saved.getPerConsumption()), 0);
```  

### 2. No Session问题  

使用存在表关联的情况下，JPA会存在`No Session`的问题，为了解决这个问题只需要在
对应的测试方法或测试类上加上`Transactional`注解即可。  

# 发布构建  

### 1. 基于Docker发布  

为了能够支持`docker`插件，需要在`maven`的配置文件`settings.xml`中增加如下
内容：  

```xml
  <pluginGroups>
    <pluginGroup>com.spotify</pluginGroup>
  </pluginGroups>
```  

完成后我们在需要使用Docker进行发布的应用`pom.xml`中增加如下内容：  

```xml
<!-- Docker maven plugin -->
<plugin>
<groupId>com.spotify</groupId>
<artifactId>docker-maven-plugin</artifactId>
<version>1.2.2</version>
<configuration>
        <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
        <dockerDirectory>src/main/docker</dockerDirectory>
        <resources>
        <resource>
                <targetPath>/</targetPath>
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
        </resource>
        </resources>
</configuration>
</plugin>
<!-- Docker maven plugin -->
```  

以及：  

```xml
<properties>
        <docker.image.prefix>theme</docker.image.prefix>
</properties>
```  

最后我们还需要自行编写对应的`Dockerfile`文件，由于配置中已经指定了对应的配置
文件所在的目录，所以我们需要在项目目录下的`src/main/docker`新建一个`Dockerfile`
文件，并在其中写入如下内容：  

```yaml
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY bortus-manager-0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

最终发布后可以通过设置环境变量`SPRING_PROFILES_ACTIVE`来决定具体使用的配置。  


# 服务监控  

### Elastic-APM  

为了保障应用的监控，本平台已植入了`elastic-apm`组建服务，为了保障开发人员对其可以快捷
使用，本教程将给出具体的说明。首先在`resources`文件夹下存在一个`elasticapm`配置文件，
该文件提供了对应开发测试环境的配置，如果读者希望生产环境也采用这种方式进行配置可以在实际
发布中通过环境变量调整其对应配置文件。环境变量名为`ELASTIC_APM_CONFIG_FILE`其中的默认值
设置为`_AGENT_HOME_/elasticapm.properties`，读者可以根据环境调整为`_AGENT_HOME_/elasticapm-prod.properties`
等各类环境的配置文件。  

如果读者不希望通过配置文件进行设置也可以通过使用环境变量进行独立的设置，具体的环境变量参数
如下所示。  

* ELASTIC_APM_SERVICE_NAME  
* ELASTIC_APM_APPLICATION_PACKAGES
* ELASTIC_APM_SERVER_URL
* ELASTIC_APM_SECRET_TOKEN  

其他更多的参数可以参考[官方文档](https://www.elastic.co/guide/en/apm/agent/java/1.x/configuration.html)  

注意，在实际开发中会无法根据编码读取正确编码，这里可以在应用启动的时候采用手动写入系统参数以及通过
代码植入的方式进行监控。  

```java
public static void main(String[] args) {
    System.setProperty("sun.stdout.encoding", "UTF-8");
    System.setProperty("sun.stderr.encoding", "UTF-8");
    ElasticApmAttacher.attach();
    SpringApplication.run(AppRun.class, args);
}
```