# HBase插件  

## 1. 读取插件  

### 1.1 配置示例  

```json
{
    "queryServerAddress": "http://127.0.0.1:8765",
    "serialization": "PROTOBUF",
    "table": "TEST",
    "column": ["ID", "NAME"],
    "splitKey": "ID"
}
```

### 1.2 参数说明  

* **queryServerAddress**

	* 描述：hbase20xsqlreader需要通过Phoenix轻客户端去连接Phoenix QueryServer，因此这里需要填写对应QueryServer地址。
 
	* 必选：是 <br />
 
	* 默认值：无 <br />
 
* **serialization**
 
	* 描述：QueryServer使用的序列化协议
 
	* 必选：否 <br />
 
	* 默认值：PROTOBUF <br />
	
* **table**

	* 描述：所要读取表名
	
	* 必选：是 <br />
 
	* 默认值：无 <br />
	
* **schema**

	* 描述：表所在的schema
	
	* 必选：否 <br />
 
	* 默认值：无 <br />
		
* **column**

	* 描述：填写需要从phoenix表中读取的列名集合，使用JSON的数组描述字段信息，空值表示读取所有列。
 
	* 必选： 否<br />
 
	* 默认值：全部列 <br />
 
* **splitKey**

	* 描述：读取表时对表进行切分并行读取，切分时有两种方式：1.根据该列的最大最小值按照指定channel个数均分，这种方式仅支持整形和字符串类型切分列；2.根据设置的splitPoint进行切分
	
	* 必选：是 <br />
 
	* 默认值：无 <br />

* **splitPoints**

	* 描述：由于根据切分列最大最小值切分时不能保证避免数据热点，splitKey支持用户根据数据特征动态指定切分点，对表数据进行切分。建议切分点根据Region的startkey和endkey设置，保证每个查询对应单个Region
 
	* 必选： 否<br />
 
	* 默认值：无 <br />
	
* **where**
    
    * 描述：支持对表查询增加过滤条件，每个切分都会携带该过滤条件。
     
    * 必选： 否<br />
     
    * 默认值：无<br />
    
* **querySql**
        
    * 描述：支持指定多个查询语句，但查询列类型和数目必须保持一致，用户可根据实际情况手动输入表查询语句或多表联合查询语句，设置该参数后，除queryserverAddress参数必须设置外，其余参数将失去作用或可不设置。
         
    * 必选： 否<br />
         
    * 默认值：无<br />

## 2. 写入插件  

### 2.1 配置示例  

```json
{
    "batchSize": "100",
    "column": [
        "UID",
        "TS",
        "EVENTID",
        "CONTENT"
    ],
    "queryServerAddress": "http://127.0.0.1:8765",
    "nullMode": "skip",
    "table": "目标hbase表名，大小写有关"
}
```

### 2.2 参数说明

* **name**

   * 描述：插件名字，必须是`hbase11xsqlwriter`
   * 必选：是
   * 默认值：无
   
* **schema**

	* 描述：表所在的schema
	
	* 必选：否 <br />
 
	* 默认值：无 <br />
	
* **table**

   * 描述：要导入的表名，大小写敏感，通常phoenix表都是**大写**表名
   * 必选：是
   * 默认值：无

* **column**

   * 描述：列名，大小写敏感，通常phoenix的列名都是**大写**。
       * 需要注意列的顺序，必须与reader输出的列的顺序一一对应。
       * 不需要填写数据类型，会自动从phoenix获取列的元数据
   * 必选：是
   * 默认值：无

* **queryServerAddress**

   * 描述：Phoenix QueryServer地址，为必填项，格式：http://${hostName}:${ip}，如http://172.16.34.58:8765
   * 必选：是
   * 默认值：无
   
* **serialization**
 
    * 描述：QueryServer使用的序列化协议
	* 必选：否 
	* 默认值：PROTOBUF
	
* **batchSize**

   * 描述：批量写入的最大行数
   * 必选：否
   * 默认值：256

* **nullMode**

   * 描述：读取到的列值为null时，如何处理。目前有两种方式：
      * skip：跳过这一列，即不插入这一列(如果该行的这一列之前已经存在，则会被删除)
      * empty：插入空值，值类型的空值是0，varchar的空值是空字符串
   * 必选：否
   * 默认值：skip