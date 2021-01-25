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



### 2.2 参数说明