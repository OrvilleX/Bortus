package com.orvillex.bortus.datapump.executor.mongo.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.mongo.MongoUtil;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class MongoDBReader extends ReaderTask {
    public static final String DOCUMENT_TYPE = "document";
    public static final String ARRAY_TYPE = "array";
    public static final String DOCUMENT_ARRAY_TYPE = "document.array";

    private ReaderParam readerConfig;
    private MongoClient mongoClient;
    private String userName = null;
    private String password = null;
    private String authDb = null;
    private String database = null;
    private String collection = null;
    private String query = null;
    private List<ReaderParam.ColumnEntry> mongodbColumnMeta = null;

    @Override
    public void startRead(RecordSender recordSender) {
        if (mongoClient == null || database == null || collection == null || mongodbColumnMeta == null) {
            throw new DataPumpException("参数不合法");
        }
        MongoDatabase db = mongoClient.getDatabase(database);
        MongoCollection<Document> col = db.getCollection(this.collection);

        MongoCursor<Document> dbCursor = null;
        Document filter = new Document();
        if (!StringUtils.isNotBlank(query)) {
            Document queryFilter = Document.parse(query);
            filter = new Document("$and", Arrays.asList(filter, queryFilter));
        }
        dbCursor = col.find(filter).iterator();
        while (dbCursor.hasNext()) {
            Document item = dbCursor.next();
            Record record = recordSender.createRecord();
            for (ReaderParam.ColumnEntry entry : mongodbColumnMeta) {
                Object tempCol = item.get(entry.getName());
                if (tempCol == null) {
                    if (entry.getName().startsWith(DOCUMENT_TYPE)) {
                        String[] name = entry.getName().split("\\.");
                        if (name.length > 1) {
                            Object obj;
                            Document nestedDocument = item;
                            for (String str : name) {
                                obj = nestedDocument.get(str);
                                if (obj instanceof Document) {
                                    nestedDocument = (Document) obj;
                                }
                            }
                            if (null != nestedDocument) {
                                Document doc = nestedDocument;
                                tempCol = doc.get(name[name.length - 1]);
                            }
                        }
                    }
                }
                if (tempCol == null) {
                    record.addColumn(new StringColumn(null));
                } else if (tempCol instanceof Double) {
                    record.addColumn(new DoubleColumn((Double) tempCol));
                } else if (tempCol instanceof Boolean) {
                    record.addColumn(new BoolColumn((Boolean) tempCol));
                } else if (tempCol instanceof Date) {
                    record.addColumn(new DateColumn((Date) tempCol));
                } else if (tempCol instanceof Integer) {
                    record.addColumn(new LongColumn((Integer) tempCol));
                } else if (tempCol instanceof Long) {
                    record.addColumn(new LongColumn((Long) tempCol));
                } else {
                    if (ARRAY_TYPE.equals(entry.getType()) || DOCUMENT_ARRAY_TYPE.equals(entry.getType())) {
                        String splitter = entry.getSplitter();
                        if (StringUtils.isNotBlank(splitter)) {
                            throw new DataPumpException("参数不合法");
                        } else {
                            ArrayList array = (ArrayList) tempCol;
                            String tempArrayStr = Joiner.on(splitter).join(array);
                            record.addColumn(new StringColumn(tempArrayStr));
                        }
                    } else {
                        record.addColumn(new StringColumn(tempCol.toString()));
                    }
                }
            }
            recordSender.sendToWriter(record);
        }
    }

    @Override
    public void init() {
        this.readerConfig = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        this.userName = readerConfig.getUsername();
        this.password = readerConfig.getPassword();
        this.database = readerConfig.getDatabase();
        this.authDb = readerConfig.getAuthdb();
        if (!StringUtils.isNotBlank(userName) && !StringUtils.isNotBlank(password)) {
            mongoClient = MongoUtil.initCredentialMongoClient(readerConfig, userName, password, authDb);
        } else {
            mongoClient = MongoUtil.initMongoClient(readerConfig);
        }

        this.collection = readerConfig.getCollectionName();
        this.query = readerConfig.getQuery();
        this.mongodbColumnMeta = readerConfig.getColumn();
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
