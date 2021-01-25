# 二次开发入门  

## 仓储部分  

### 仓储开发  

* 使用自定义结果  

由于仓储层本身使用了JPA框架，在实际研发过程中往往需要返回非Entity对象，为了满足这类需求用户可以采用自定义Pojo的方式，然后通过在`Query`中添加对应代码即可，比如下面这种方式：  

```java
@Query(value = "select new com.Vo.CustomModel(a.bookpkid,a.bookcode,a.bookname,b.authorname) " +
        "FROM BookInfo a LEFT JOIN AuthorInfo b ON a.authorid=b.authorid WHERE a.bookpkid=:pkid")
List<CustomModel> selectModelByPkid(@Param("pkid") BigDecimal pkid);
```

其中需要注意模型必须采用全路径的格式，否则会无法找到具体的类，当然这类方式存在一个致命的缺点就是该类发生变化或者需要修改则无法及时修正，为此建议读者使用`EntityUtils`工具类提供的方法进行转换，而仓储层采用返回`Object[]`的方式接收单个对象，或者`List<Object[]>`接收多个自定义对象，并采用`T caseEntity(Object[] entity, Class<T> clazz)`进行转换即可。  