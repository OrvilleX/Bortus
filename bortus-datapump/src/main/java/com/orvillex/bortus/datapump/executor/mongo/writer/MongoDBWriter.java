package com.orvillex.bortus.datapump.executor.mongo.writer;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.mongo.MongoUtil;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

public class MongoDBWriter extends WriterTask {
    public static final String ARRAY_TYPE = "array";
    public static final String OBJECT_ID_TYPE = "objectid";

    private WriteParam writerConfig;
    private MongoClient mongoClient;
    private String userName = null;
    private String password = null;
    private String database = null;
    private String collection = null;
    private Integer batchSize = null;
    private List<WriteParam.ColumnEntry> mongodbColumnMeta = null;
    private WriteParam.WriteMode writeMode = null;
    private static int BATCH_SIZE = 1000;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        if (StringUtils.isNotBlank(database) || StringUtils.isNotBlank(collection) || mongoClient == null
                || mongodbColumnMeta == null || batchSize == null) {
            throw new DataPumpException("参数错误");
        }
        MongoDatabase db = mongoClient.getDatabase(database);
        MongoCollection<BasicDBObject> col = db.getCollection(this.collection, BasicDBObject.class);
        List<Record> writerBuffer = new ArrayList<Record>(this.batchSize);
        Record record = null;
        while ((record = lineReceiver.getFromReader()) != null) {
            writerBuffer.add(record);
            if (writerBuffer.size() >= this.batchSize) {
                doBatchInsert(col, writerBuffer, mongodbColumnMeta);
                writerBuffer.clear();
            }
        }
        if (!writerBuffer.isEmpty()) {
            doBatchInsert(col, writerBuffer, mongodbColumnMeta);
            writerBuffer.clear();
        }
    }

    private void doBatchInsert(MongoCollection<BasicDBObject> collection, List<Record> writerBuffer,
            List<WriteParam.ColumnEntry> columnMeta) {
        List<BasicDBObject> dataList = new ArrayList<BasicDBObject>();
        for (Record record : writerBuffer) {
            BasicDBObject data = new BasicDBObject();
            for (int i = 0; i < record.getColumnNumber(); i++) {
                String type = columnMeta.get(i).getType();
                if (StringUtils.isNotBlank(record.getColumn(i).asString())) {
                    if (ARRAY_TYPE.equals(type.toLowerCase())) {
                        data.put(columnMeta.get(i).getName(), new Object[0]);
                    } else {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asString());
                    }
                    continue;
                }
                if (Column.Type.INT.name().equalsIgnoreCase(type)) {
                    try {
                        data.put(columnMeta.get(i).getName(),
                                Integer.parseInt(String.valueOf(record.getColumn(i).getRawData())));
                    } catch (Exception e) {
                        this.getTaskCollector().collectDirtyRecord(record, e);
                    }
                } else if (record.getColumn(i) instanceof StringColumn) {
                    try {
                        if (OBJECT_ID_TYPE.equals(type.toLowerCase())) {
                            data.put(columnMeta.get(i).getName(), new ObjectId(record.getColumn(i).asString()));
                        } else if (ARRAY_TYPE.equals(type.toLowerCase())) {
                            String splitter = columnMeta.get(i).getSplitter();
                            if (StringUtils.isNotBlank(splitter)) {
                                throw new DataPumpException("参数错误");
                            }
                            String itemType = columnMeta.get(i).getItemType();
                            if (itemType != null && !itemType.isEmpty()) {
                                String[] item = record.getColumn(i).asString().split(splitter);
                                if (itemType.equalsIgnoreCase(Column.Type.DOUBLE.name())) {
                                    ArrayList<Double> list = new ArrayList<Double>();
                                    for (String s : item) {
                                        list.add(Double.parseDouble(s));
                                    }
                                    data.put(columnMeta.get(i).getName(), list.toArray(new Double[0]));
                                } else if (itemType.equalsIgnoreCase(Column.Type.INT.name())) {
                                    ArrayList<Integer> list = new ArrayList<Integer>();
                                    for (String s : item) {
                                        list.add(Integer.parseInt(s));
                                    }
                                    data.put(columnMeta.get(i).getName(), list.toArray(new Integer[0]));
                                } else if (itemType.equalsIgnoreCase(Column.Type.LONG.name())) {
                                    ArrayList<Long> list = new ArrayList<Long>();
                                    for (String s : item) {
                                        list.add(Long.parseLong(s));
                                    }
                                    data.put(columnMeta.get(i).getName(), list.toArray(new Long[0]));
                                } else if (itemType.equalsIgnoreCase(Column.Type.BOOL.name())) {
                                    ArrayList<Boolean> list = new ArrayList<Boolean>();
                                    for (String s : item) {
                                        list.add(Boolean.parseBoolean(s));
                                    }
                                    data.put(columnMeta.get(i).getName(), list.toArray(new Boolean[0]));
                                } else if (itemType.equalsIgnoreCase(Column.Type.BYTES.name())) {
                                    ArrayList<Byte> list = new ArrayList<Byte>();
                                    for (String s : item) {
                                        list.add(Byte.parseByte(s));
                                    }
                                    data.put(columnMeta.get(i).getName(), list.toArray(new Byte[0]));
                                } else {
                                    data.put(columnMeta.get(i).getName(),
                                            record.getColumn(i).asString().split(splitter));
                                }
                            } else {
                                data.put(columnMeta.get(i).getName(), record.getColumn(i).asString().split(splitter));
                            }
                        } else if (type.toLowerCase().equalsIgnoreCase("json")) {
                            Object mode = com.mongodb.util.JSON.parse(record.getColumn(i).asString());
                            data.put(columnMeta.get(i).getName(), JSON.toJSON(mode));
                        } else {
                            data.put(columnMeta.get(i).getName(), record.getColumn(i).asString());
                        }
                    } catch (Exception e) {
                        this.getTaskCollector().collectDirtyRecord(record, e);
                    }
                } else if (record.getColumn(i) instanceof LongColumn) {
                    if (Column.Type.LONG.name().equalsIgnoreCase(type)) {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asLong());
                    } else {
                        this.getTaskCollector().collectDirtyRecord(record,
                                "record's [" + i + "] column's type should be: " + type);
                    }
                } else if (record.getColumn(i) instanceof DateColumn) {
                    if (Column.Type.DATE.name().equalsIgnoreCase(type)) {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asDate());
                    } else {
                        super.getTaskCollector().collectDirtyRecord(record,
                                "record's [" + i + "] column's type should be: " + type);
                    }
                } else if (record.getColumn(i) instanceof DoubleColumn) {
                    if (Column.Type.DOUBLE.name().equalsIgnoreCase(type)) {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asDouble());
                    } else {
                        this.getTaskCollector().collectDirtyRecord(record,
                                "record's [" + i + "] column's type should be: " + type);
                    }
                } else if (record.getColumn(i) instanceof BoolColumn) {
                    if (Column.Type.BOOL.name().equalsIgnoreCase(type)) {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asBoolean());
                    } else {
                        super.getTaskCollector().collectDirtyRecord(record,
                                "record's [" + i + "] column's type should be: " + type);
                    }
                } else if (record.getColumn(i) instanceof BytesColumn) {

                    if (Column.Type.BYTES.name().equalsIgnoreCase(type)) {
                        data.put(columnMeta.get(i).getName(), record.getColumn(i).asBytes());
                    } else {
                        super.getTaskCollector().collectDirtyRecord(record,
                                "record's [" + i + "] column's type should be: " + type);
                    }

                } else {
                    data.put(columnMeta.get(i).getName(), record.getColumn(i).asString());
                }
            }
            dataList.add(data);
        }

        /**
         * 如果存在重复的值覆盖
         */
        if (this.writeMode != null && this.writeMode.getIsReplace()) {
            String uniqueKey = this.writeMode.getReplaceKey();
            if (!StringUtils.isNotBlank(uniqueKey)) {
                List<ReplaceOneModel<BasicDBObject>> replaceOneModelList = new ArrayList<ReplaceOneModel<BasicDBObject>>();
                for (BasicDBObject data : dataList) {
                    BasicDBObject query = new BasicDBObject();
                    if (uniqueKey != null) {
                        query.put(uniqueKey, data.get(uniqueKey));
                    }
                    ReplaceOneModel<BasicDBObject> replaceOneModel = new ReplaceOneModel<BasicDBObject>(query, data,
                            new UpdateOptions().upsert(true));
                    replaceOneModelList.add(replaceOneModel);
                }
                collection.bulkWrite(replaceOneModelList, new BulkWriteOptions().ordered(false));
            } else {
                throw new DataPumpException("参数不合法");
            }
        } else {
            collection.insertMany(dataList);
        }
    }

    @Override
    public void init() {
        this.writerConfig = JSON.parseObject(this.getTriggerParam(), WriteParam.class);
        this.userName = writerConfig.getUsername();
        this.password = writerConfig.getPassword();
        this.database = writerConfig.getDatabase();
        if (!StringUtils.isNotBlank(userName) && !StringUtils.isNotBlank(password)) {
            this.mongoClient = MongoUtil.initCredentialMongoClient(this.writerConfig, userName, password, database);
        } else {
            this.mongoClient = MongoUtil.initMongoClient(this.writerConfig);
        }
        this.collection = writerConfig.getCollectionName();
        this.batchSize = BATCH_SIZE;
        this.mongodbColumnMeta = writerConfig.getColumn();
        this.writeMode = writerConfig.getWriteMode();
    }

    @Override
    public void destroy() {
        mongoClient.close();
    }
}
