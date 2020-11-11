# 源码解析  

## Spring Security安全  

### 基本概述  

本框架使用了官方的`spring-boot-starter-security`提供具体的用户权限控制，通过`eladmin`官方网站我们可以看到接口中可以支持我们使用以下
两种方式来决定接口的授权控制：  

* hasRole([role])：当前用户是否拥有指定的角色;  
* hasAnyRole([role1,role2])：多个角色是以逗号分隔，如果当前用户拥有指定角色中的任意一个则返回true;  

具体使用的示例代码如下：  

```java
@Log(description = "修改菜单")
@PutMapping(value = "/menus")
@PreAuthorize("hasAnyRole('admin','menu:edit')")
public ResponseEntity update(@Validated @RequestBody Menu resources){
    // 略
}
```  

由于实际生活中往往具备超级管理员权限，所有其支持使用自定义的方式进行灵活的权限判断，具体按照如下方式进行使用即可：  


```java
@PreAuthorize("@el.check('user:list','user:add')") 
```  

当然我们可以看到其中使用`PreAuthorize`注解进行了判断，该注解属性采用了对应安全框架自带的方式，但是其中的用户信息的鉴权等方式已经改用
了JWT方式进行具体的提供，下面我们将对其开展来介绍其中的具体实现方式。  


### SecurityConfig  

该类存在于`me.zhengjie.modules.security.config`包下，为安全框架的主要配置入口，其继承了`WebSecurityConfigurerAdapter`框架，具体
可以参考其中的`configure`方法，方法其中主要设置了可以开放访问的路径资源，最后一行代码指定了新的安全配置：  

```java
apply(securityConfigurerAdapter())
```

其中的`securityConfigurerAdapter`方法如下：  

```java
private TokenConfigurer securityConfigurerAdapter() {
    return new TokenConfigurer(tokenProvider, properties, onlineUserService, userCacheClean);
}
```  

### TokenConfigurer  

`securityConfigurerAdapter`中返回的实际对象如下所示：  

```java
@RequiredArgsConstructor
public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;
    private final OnlineUserService onlineUserService;
    private final UserCacheClean userCacheClean;

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(tokenProvider, properties, onlineUserService, userCacheClean);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```  

其中可以看到其具体的基类`SecurityConfigurerAdapter`中也需要实现对应的`configure`方法，并且通过将我们自定义的过滤器添加到框架
本身的过滤器在`UsernamePasswordAuthenticationFilter`之前进行处理。在具体介绍`TokenFilter`过滤器之前我们首先了解其构造函数对应的四个入参：  

* TokenProvider  
* SecurityProperties  
* OnlineUserService  
* UserCacheClean  

### SecurityProperties  

根据配置文件加载对应的JWT所需的配置信息，其中各参数的说明如下：  

* header(jwt.header): 代表令牌在http头部中的名称;  
* tokenStartWith(jwt.token-start-with): 代表令牌前缀的字符串，具体令牌会在其空格后追加;  
* base64Secret(jwt.base64-secret): JWT令牌编码私钥;  
* tokenValidityInSeconds(jwt.token-validity-in-seconds): 令牌过期时间，单位为毫秒;  
* onlineKey(jwt.online-key): 在线用户在Redis中的前缀;  
* codeKey(jwt.code-key): 验证码在Redis中的前缀;  
* detect(jwt.detect): Token续期检查时间范围，在token即将过期的一段时间内用户操作了则给用户的token续期;  
* renew(jwt.renew): 续期时间范围，单位为毫秒;  

### TokenProvider  

提供JWT的生成以及解码，其采用了`io.jsonwebtoken`的三个类库提供了具体的功能支持，需要如下所列：  

* jjwt-api  
* jjwt-impl  
* jjwt-jackson  

其中存在无法通过Spring本身自带的注入方式进行初始化的对象，所以该类继承了`InitializingBean`接口，通过其提供的`afterPropertiesSet`方法对`jwtParser`和`jwtBuilder`进行了初始化，前者用于对token进行解码，后者将生成对应的token令牌。通过观察可以看到该类提供了以下四种主要方法以及对应的用途：  

```java
// 根据对应的角色以及用户名生成令牌信息
String createToken(Authentication authentication)

// 从Token解析中对应的角色以及用户名
Authentication getAuthentication(String token)

// 检查令牌，并对在续期范围内的令牌进行续期
void checkRenewal(String token)

// 从http请求中获取令牌
String getToken(HttpServletRequest request)
```

以上就是该类提供的各类功能。  

### OnlineUserService  

本框架额外还提供了在线用户的记录功能，其利用Redis进行数据的保存。其中主要存储的数据如下：  

```java
public class OnlineUserDto {
    private String userName;  // 用户名
    private String nickName;  // 昵称
    private String dept;  // 岗位
    private String browser;  // 浏览器
    private String ip;  // IP
    private String address;  // 地址
    private String key;  // token
    private Date loginTime;  // 登录时间
}
```  

为了便于查询在线的用户以及相关的用户操作，该类提供了如下方法便于操作。  

```java
// 保存在线用户信息
void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request)

// 查询全部数据
Map<String,Object> getAll(String filter, Pageable pageable)

// 查询全部数据（不分页）
List<OnlineUserDto> getAll(String filter)

// 踢出用户
void kickOut(String key)

// 退出登录
void logout(String token)

// 导出
void download(List<OnlineUserDto> all, HttpServletResponse response)

// 查询用户
OnlineUserDto getOne(String key)

// 检测用户是否在之前已经登录，已经登录踢下线
void checkLoginOnUser(String userName, String igoreToken)

// 根据用户名强退用户
void kickOutForUsername(String username)
```

### UserCacheClean

用于清理用户登录信息缓存，其提供了根据用户名以及全清除的方法，其内部本身是通过`UserDetailsServiceImpl`类其中的静态变量`userDtoCache`来提供实现的。  

```java
public class UserCacheClean {

    public void cleanUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            UserDetailsServiceImpl.userDtoCache.remove(userName);
        }
    }

    public void cleanAll() {
        UserDetailsServiceImpl.userDtoCache.clear();
    }
}
```  

而`userDtoCache`主要是通过该类的`loadUserByUsername`方法对其数据进行添加存储的，该方法首先从缓存读取，如果无法读取则通过`UserService`进行读取，并且只有存在用户，并且用户可用的情况下利用`DataService`和`RoleService`填充其用户信息中的岗位以及权限至`JwtUserDto`中并最终存储到缓存中。  

```java
jwtUserDto = new JwtUserDto(
        user,
        dataService.getDeptIds(user),
        roleService.mapToGrantedAuthorities(user)
);
userDtoCache.put(username, jwtUserDto);
```  

`UserDetailsServiceImpl`类本身是继承自`UserDetailsService`，加之其注解属性中强制替换了默认实现`@Service("userDetailsService")`。所以在登录进行用户鉴权的时候Spring Security本身其实是调用的该类进行判断。  

`AuthorizationController/Login`
```java
authenticationManagerBuilder.getObject().authenticate(authenticationToken)
```  

### TokenFilter  

最后我们就需要讲解该类了，其中将以上四个类进行的注入并对进入的HTTP请求进行过滤处理，其核心的计算就在其`doFilter`方法中。  

该方法首先从`HttpServletRequest`对象的请求头部中将Token信息进行获取，再通过`OnlineUserSerivice`获取该用户在线登录的状态，如果令牌超时则将通过`UserCacheClean`清除该用户的缓存信息。  

```java
if (cleanUserCache || Objects.isNull(onlineUserDto)) {
    userCacheClean.cleanUserCache(String.valueOf(tokenProvider.getClaims(token).get(TokenProvider.AUTHORITIES_KEY)));
}
```  

如果获取到正确信息则通过`TokenProvider`获取其权限信息并将对应的权限信息赋值到Spring Security框架中。  

```java
Authentication authentication = tokenProvider.getAuthentication(token);
SecurityContextHolder.getContext().setAuthentication(authentication);
```  

最后为了防止令牌即将到期，最后还需要对令牌进行续期操作。上面整个操作就是完整的过滤器的操作行为了。  

### SecurityUtils  

在实际业务的开发中必然是需要获取当前已经登录的用户信息的，可以通过静态方法`getCurrentUser`获取到具体的用户信息，如果不存在则会抛出异常信息。其提供的主要方法如下。  

```java
// 获取当前登录的用户
UserDetails getCurrentUser()

// 获取系统用户名称
String getCurrentUsername()

// 获取用户ID
Long getCurrentUserId()

// 获取当前用户的数据权限
List<Long> getCurrentUserDataScope()

// 获取数据权限级别
String getDataScopeType()
```

## Spring Data Jpa数据访问  

该框架为了便于对数据的查询不仅仅使用了Jpa，同时还提供了`Query`注解以及`QueryHelp`对象便于提供更复杂的查询功能的支持，以下除了会对基础的功能提供介绍以外还会介绍`JpaSpecificationExecutor`提供给我们的额外查询功能支持。  

### NoRepositoryBean  

由于JPA默认会将继承自`Repository`的对象默认注册为Bean，为了防止注册不必要的Bean所以提供了该注解通过使用这类注解可以防止注册多余的Bean。可以看到`JpaRepository<T, ID>`等均使用了该注解，如果我们需要实现自己的`BaseRepositor`，同时不希望将其自动注册为Bean则需要使用该注解。  

```java
@NoReposiroryBean
public class BaseRepository<T, ID> extends JpaRepository<T, ID> {

}
```  

### Query  

其主要提供了查询对象中各个字段的查询方式，从而便于对其进行反射从而形成所需要的查询表达式，这里主要介绍下几个关键的属性。  

* propName：属性名，默认采用字段名；  
* type：查询条件，默认为全等；  
* joinName：连接查询的属性名；  
* join：连接方式，默认为左连接；  
* blurry：多字段默认查询字段，采用逗号分隔需要同时模糊查询的字段；  

以上就是其主要提供的类型，当然读者可以自行扩展，但是对应的也需要对`QueryHelp`进行扩展从而支持新属性的解析。  

### QueryHelp  

其实本框架本身并没有独自创造轮子，其核心是围绕`JpaSpecificationExecutor`接口中的以下两个接口出发提供了基于以上字段属性的反射解析功能。   

```java
Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable);
List<T> findAll(@Nullable Specification<T> spec, Sort sort);
```  

通过以上两个函数我们可以看到其中主要通过`Specification`提供了实际的查询条件的支持，并通过对其接口进行剖析可以发现其中只有一个接口并未提供实现，其方法如下。  

```java
Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
```

后续我们可以看到在我们的service中都会使用lambda实现以上方法，并在内部使用`QueryHelp`的`getPredicate`提供具体的查询方式。  

```java
@Override
public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
    Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(userMapper::toDto));
}
```  

通过以上代码可以很容易的看到通过`QueryHelp`可以省去我们通过`CriteriaBuilder`实际拼写查询表达式的过程。直接根据查询对象中的各个字段的`Query`注解属性进行实际的解析，以下截取了部分代码片段。  

```java
if (ObjectUtil.isNotEmpty(joinName)) {
    String[] joinNames = joinName.split(">");
    for (String name : joinNames) {
        switch (q.join()) {
            case LEFT:
                if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
                    join = join.join(name, JoinType.LEFT);
                } else {
                    join = root.join(name, JoinType.LEFT);
                }
                break;
                // to do...
        }
    }
}
switch (q.type()) {
    case EQUAL:
        list.add(cb.equal(getExpression(attributeName,join,root)
            .as((Class<? extends Comparable>) fieldType),val));
        break;
    case GREATER_THAN:
        list.add(cb.greaterThanOrEqualTo(getExpression(attributeName,join,root)
            .as((Class<? extends Comparable>) fieldType), (Comparable) val));
        break;
    case LESS_THAN:
        list.add(cb.lessThanOrEqualTo(getExpression(attributeName,join,root)
            .as((Class<? extends Comparable>) fieldType), (Comparable) val));
        break;
        // to do...
}
```  

为了弥补大家的知识，如果读者对`JpaSpecificationExecutor`感兴趣，可以通过以下文章更好的了解`Specifications`查询，[点击此处](https://www.jianshu.com/p/d9a99b95d094)。其中大量使用的官方提供的相关API对象，关于API的内容介绍可以[点击此处](https://docs.oracle.com/javaee/6/api/javax/persistence/criteria/package-summary.html)。  

数据库查询的数据往往无法直接进行输出，均为采用DTO进行实际的输出，而本框架采用了成熟的`mapstruct`框架提供了这部分功能，感兴趣的读者可以读取本[文档](https://mapstruct.org/)  

## Spring Redis Cache缓存  

本框架采用了官方的Spring Boot Cache提供了基于方法级别的缓存，如果读者需要了解更多的知识可以参考[该文章](https://www.cnblogs.com/ejiyuan/p/11014765.html)。对于需要自行访问并进行相关操作可以通过注入`RedisUtils`对象进行访问即可。  

对于Cache的配置可以参考`eladmin-common`中的`RedisConfig`文件，其中覆盖了默认的Key与Value的序列化方式，并且对自动缓存的Key生成规则也进行了重置。  

## 数据权限  



## 异常  


