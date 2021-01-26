package com.orvillex.bortus.datapump.executor.hbase.writer;

import java.util.List;

import lombok.Data;

@Data
public class WriterParam {
    private String table;
    private String queryServerAddress;
    private String nullMode = "skip";
    /**
     * QueryServer使用的序列化协议
     */
    private String serialization = "PROTOBUF";

    /**
     * 填写需要从phoenix表中读取的列名集合
     */
    private List<String> column;

    /**
     * 表所在的schema
     */
    private String schema;

    private Integer batchSize = 256;
}
