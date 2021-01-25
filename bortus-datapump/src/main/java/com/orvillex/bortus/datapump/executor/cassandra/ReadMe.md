# Cassandra数据库组件  

## 1. 介绍  



## 2. 读取配置  

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

## 3. 写入配置  

### 3.1 参数示例  

参数采用`ReaderParam`类型，其中具体的转换为Json格式如下：

```json
{
    "host": "localhost",
    "port": 9042,
    "useSSL": false,
    "keyspace": "stresscql",
    "table": "dst",
    "batchSize":10,
    "column": [
        "name",
        "choice",
        "date",
        "address",
        "dbl",
        "lval",
        "fval",
        "ival",
        "uid",
        "value",
        "listval"
    ]
}
```

### 3.2 参数说明  

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

* **connectionsPerHost**

	* 描述：客户端连接池配置：与服务器每个节点建多少个连接。<br />

	* 必选：否 <br />

	* 默认值：8 <br />

* **maxPendingPerConnection**

	* 描述：客户端连接池配置：每个连接最大请求数。<br />

	* 必选：否 <br />

	* 默认值：128 <br />

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
	  内容可以是列的名称或"writetime()"。如果将列名配置为writetime()，会将这一列的内容作为时间戳。

	* 必选：是 <br />

	* 默认值：无 <br />


* **consistancyLevel**

	* 描述：数据一致性级别。可选ONE|QUORUM|LOCAL_QUORUM|EACH_QUORUM|ALL|ANY|TWO|THREE|LOCAL_ONE<br />

	* 必选：否 <br />

	* 默认值：LOCAL_QUORUM <br />

* **batchSize**

	* 描述：一次批量提交(UNLOGGED BATCH)的记录数大小（条数）。注意batch的大小有如下限制：<br />
	  （1）不能超过65535。<br />
	   (2) batch中的内容大小受到服务器端batch_size_fail_threshold_in_kb的限制。<br />
	   (3) 如果batch中的内容超过了batch_size_warn_threshold_in_kb的限制，会打出warn日志，但并不影响写入，忽略即可。<br />
	   如果批量提交失败，会把这个批量的所有内容重新逐条写入一遍。

	* 必选：否 <br />

	* 默认值：1 <br />
