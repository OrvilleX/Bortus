package com.orvillex.bortus.datapump.executor.elasticsearc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.datapump.core.element.Column;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.exception.DataPumpException;

/**
 * ElasticSearch写入
 * @author y-z-f
 * @version 0.1
 */
public class ElasticSearchWriter extends WriterTask {
    public static volatile ESImport esImport;
    private WriterParam writerParam = null;
    private String index = "";
    private String type = "";
    private String idField = "";
    private List<String> column = null;
    private int concurrent = 1;
    private int bulkNum = 1000;
    private boolean refresh = false;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        Record record = null;
        while (true) {
            record = lineReceiver.getFromReader();
            if (record == null) {
                lineReceiver.shutdown();
                return;
            }
            if (this.column.size() > record.getColumnNumber()) {
                throw new DataPumpException("配置文件column数量大于reader column数量");
            }
            Map<String, Object> data = new HashMap<>();
            for (int i = 0; i < this.column.size(); i++) {
                Column readColumn = record.getColumn(i);
                if (readColumn != null) {
                    data.put(this.column.get(i), readColumn.getRawData());
                }
            }
            esImport.add(data);
        }
    }

    @Override
    public void init() {
        this.writerParam = JSON.parseObject(this.getTriggerParam(), WriterParam.class);
        this.index = writerParam.getIndex();
        this.type = writerParam.getType();
        this.idField = writerParam.getField();
        this.column = writerParam.getColumn();
        this.concurrent = writerParam.getConcurrent();
        this.bulkNum = writerParam.getBulkNum();
        this.refresh = writerParam.getRefresh();
        esImport = new ESImport(ESClientHelper.createClient(writerParam), this.index, this.type, this.idField)
                .setBulkNum(this.bulkNum).setConcurrent(this.concurrent).setRefresh(this.refresh).build();
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

}
