# Cassandra数据库组件  

## 1. 介绍  



## 2. 参数配置  

### 2.1 参数示例  

参数采用`ReaderParam`类型，其中具体的转换为Json格式如下：

```json
{
    "host": "localhost",
    "port": 9042,
    "useSSL": false,
    "keyspace": "test",
    "table": "datax_src",
    "column": [
        "textCol",
        "blobCol",
        "writetime(blobCol)",
        "boolCol",
        "smallintCol",
        "tinyintCol",
        "intCol",
        "bigintCol",
        "varintCol",
        "floatCol",
        "doubleCol",
        "decimalCol",
        "dateCol",
        "timeCol",
        "timeStampCol",
        "uuidCol",
        "inetCol",
        "durationCol",
        "listCol",
        "mapCol",
        "setCol"
        "tupleCol"
        "udtCol",
    ]
}
```

### 2.2 参数说明  

* **host**

	* 描述：Cassandra连接点的域名或ip，多个node之间用逗号分隔。 <br />

	* 必选：是 <br />

	* 默认值：无 <br />

* **port**

	* 描述：Cassandra端口。 <br />

	* 必选：是 <br />

	* 默认值：9042 <br />

* **username**

	* 描述：数据源的用户名 <br />

	* 必选：否 <br />

	* 默认值：无 <br />

* **password**

	* 描述：数据源指定用户名的密码 <br />

	* 必选：否 <br />

	* 默认值：无 <br />

* **useSSL**

	* 描述：是否使用SSL连接。<br />

	* 必选：否 <br />

	* 默认值：false <br />

* **keyspace**

	* 描述：需要同步的表所在的keyspace。<br />

	* 必选：是 <br />

	* 默认值：无 <br />

* **table**

	* 描述：所选取的需要同步的表。<br />

	* 必选：是 <br />

	* 默认值：无 <br />

* **column**

	* 描述：所配置的表中需要同步的列集合。<br />
	  其中的元素可以指定列的名称或writetime(column_name)，后一种形式会读取column_name列的时间戳而不是数据。

	* 必选：是 <br />

	* 默认值：无 <br />


* **where**

	* 描述：数据筛选条件的cql表达式，例如:<br />
	```
	"where":"textcol='a'"
	```

	* 必选：否 <br />

	* 默认值：无 <br />

* **allowFiltering**

	* 描述：是否在服务端过滤数据。参考cassandra文档中ALLOW FILTERING关键字的相关描述。<br />

	* 必选：否 <br />

	* 默认值：无 <br />

* **consistancyLevel**

	* 描述：数据一致性级别。可选ONE|QUORUM|LOCAL_QUORUM|EACH_QUORUM|ALL|ANY|TWO|THREE|LOCAL_ONE<br />

	* 必选：否 <br />

	* 默认值：LOCAL_QUORUM <br />