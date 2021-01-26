# MongoDB插件  

## 1. 读取插件  

### 1.1 配置示例  

```json
{
	"address": ["127.0.0.1:27017"],
	"username": "",
	"password": "",
	"database": "tag_per_data",
	"collectionName": "tag_data12",
	"column": [
	    {
	        "name": "unique_id",
	        "type": "string"
	    },
	    {
	        "name": "sid",
	        "type": "string"
	    },
	    {
	        "name": "user_id",
	        "type": "string"
	    }
	]
}
```

### 1.2 参数说明  

* address： MongoDB的数据地址信息，因为MonogDB可能是个集群，则ip端口信息需要以Json数组的形式给出。【必填】
* userName：MongoDB的用户名。【选填】
* password： MongoDB的密码。【选填】
* collectionName： MonogoDB的集合名。【必填】
* column：MongoDB的文档列名。【必填】
* name：Column的名字。【必填】
* type：Column的类型。【选填】
* splitter：因为MongoDB支持数组类型，但是Datax框架本身不支持数组类型，所以mongoDB读出来的数组类型要通过这个分隔符合并成字符串。【选填】