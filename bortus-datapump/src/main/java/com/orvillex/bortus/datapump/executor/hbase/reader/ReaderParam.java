package com.orvillex.bortus.datapump.executor.hbase.reader;

import java.util.List;

import lombok.Data;

@Data
public class ReaderParam {
    /**
     * Phoenix轻客户端地址（必填）
     */
    private String queryServerAddress;

    /**
     * QueryServer使用的序列化协议
     */
    private String serialization = "PROTOBUF";

    /**
     * 所要读取表名（必填）
     */
    private String table;

    /**
     * 表所在的schema
     */
    private String schema;

    /**
     * 填写需要从phoenix表中读取的列名集合
     */
    private List<String> column;

    /**
     * 读取表时对表进行切分并行读取（必填）
     */
    private String splitKey;

    /**
     * 动态指定切分点
     */
    private List<Object> splitPoints;

    /**
     * 支持对表查询增加过滤条件
     */
    private String where;

    /**
     * 支持指定多个查询语句
     */
    private String querySql;
}
