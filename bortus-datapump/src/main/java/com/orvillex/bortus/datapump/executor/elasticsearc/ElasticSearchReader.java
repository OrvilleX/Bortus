package com.orvillex.bortus.datapump.executor.elasticsearc;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.routing.Preference;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * ElasticSearch读取
 * @author y-z-f
 * @version 0.1
 */
public class ElasticSearchReader extends ReaderTask {
    private ReaderParam readerParam;
    private Client client;
    private String index = "";
    private String type = "";
    private List<String> column = null;
    private String scrollId;
    private TimeValue keepAlive = TimeValue.timeValueMinutes(3);
    private int pageSize = 100;
    private String shards;
    private String condition;
    private String field;

    @Override
    public void startRead(RecordSender recordSender) {
        JobLogger.log(
                "=============elasticsearch reader task start read on shards:" + this.shards + "==================");
        SearchResponse response = null;
        while (true) {
            if (StringUtils.isBlank(this.scrollId)) {
                SearchRequestBuilder builder = this.client.prepareSearch(this.index).setTypes(this.type)
                        .setScroll(this.keepAlive).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setPreference(Preference.SHARDS.type() + ":" + this.shards).setSize(this.pageSize);
                if (this.condition.equals("")) {
                    response = builder.execute().actionGet();
                } else {
                    response = builder.setQuery((QueryBuilders.wildcardQuery(this.field, this.condition))).execute()
                            .actionGet();
                }
            } else {
                response = this.client.prepareSearchScroll(this.scrollId).setScroll(this.keepAlive).execute()
                        .actionGet();
                if (response.getHits().getHits().length == 0) {
                    JobLogger.log("=================elasticsearch reader task end read======================");
                    recordSender.flush();
                    recordSender.terminate();
                    return;
                }
            }
            this.scrollId = response.getScrollId();
            SearchHit[] hits = response.getHits().getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                Map<String, Object> data = hit.getSource();
                Record record = recordSender.createRecord();
                String _id = hit.getId();
                Column _idCol = new StringColumn(_id);
                if (this.column.size() == 1 && StringUtils.equals("*", this.column.get(0))) {
                    record.addColumn(_idCol);
                    Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> entry = iterator.next();
                        Column col = getColumn(_id, entry.getKey(), entry.getValue().toString().replace("\n", ""));
                        record.addColumn(col);
                    }
                } else {
                    for (int j = 0; j < this.column.size(); j++) {
                        String key = this.column.get(j);
                        if (StringUtils.equals("_id", key)) {
                            record.addColumn(_idCol);
                        } else {
                            if (data.get(key) == null) {
                                Column col = getColumn(_id, key, "");
                                record.addColumn(col);
                            } else {
                                Column col = getColumn(_id, key, data.get(key).toString().replace("\n", ""));
                                record.addColumn(col);
                            }
                        }

                    }
                }
                recordSender.sendToWriter(record);
            }
        }
    }

    @Override
    public void init() {
        readerParam = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        this.index = readerParam.getIndex();
        this.type = readerParam.getType();
        this.column = readerParam.getColumn();
        this.pageSize = readerParam.getPageSize();
        this.shards = readerParam.getShards();
        this.client = ESClientHelper.createClient(readerParam);
        this.condition = readerParam.getCondition();
        this.field = readerParam.getField();
    }

    @Override
    public void destroy() {
        JobLogger.log("======elasticsearch reader task destroy==============");
        if (StringUtils.isNotBlank(this.scrollId)) {
            this.client.prepareClearScroll().addScrollId(this.scrollId).execute().actionGet();
        }
        this.client.close();
    }

    private Column getColumn(String _id, String key, Object value) {
        if (value == null) {
            return null;
        }
        Column col = null;
        if (value instanceof Long) {
            col = new LongColumn((Long) value);
        } else if (value instanceof Integer) {
            col = new LongColumn(((Integer) value).longValue());
        } else if (value instanceof Byte) {
            col = new LongColumn(((Byte) value).longValue());
        } else if (value instanceof Short) {
            col = new LongColumn(((Short) value).longValue());
        } else if (value instanceof String) {
            col = new StringColumn((String) value);
        } else if (value instanceof Double) {
            col = new DoubleColumn((Double) value);
        } else if (value instanceof Float) {
            col = new DoubleColumn(((Float) value).doubleValue());
        } else if (value instanceof Date) {
            col = new DateColumn((Date) value);
        } else if (value instanceof Boolean) {
            col = new BoolColumn((Boolean) value);
        } else if (value instanceof byte[]) {
            col = new BytesColumn((byte[]) value);
        } else {
            throw new DataPumpException("发生在_id:" + _id + ",key:" + key);
        }
        return col;
    }
}
