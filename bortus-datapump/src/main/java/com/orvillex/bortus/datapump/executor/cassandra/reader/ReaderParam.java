package com.orvillex.bortus.datapump.executor.cassandra.reader;

import java.util.List;

import lombok.Data;

@Data
public class ReaderParam {
    /**
     * 数据源的用户名
     */
    private String username;

    /**
     * 数据源指定用户名的密码
     */
    private String password;

    /**
     * Cassandra连接点的域名或ip,多个node之间用逗号分隔（必填）
     */
    private String host;

    /**
     * Cassandra端口（必填）
     */
    private Integer port = 9042;

    /**
     * 是否使用SSL连接
     */
    private Boolean useSSL = false;

    /**
     * 需要同步的表所在的keyspace（必填）
     */
    private String keySpace;

    /**
     * 所选取的需要同步的表（必填）
     */
    private String table;

    /**
     * 所配置的表中需要同步的列集合（必填）
     */
    private List<String> column;

    /**
     * 数据筛选条件的cql表达式
     */
    private String where;

    /**
     * 是否在服务端过滤数据,参考cassandra文档中ALLOW FILTERING关键字的相关描述
     */
    private Boolean allowFiltering;

    /**
     * 数据一致性级别。可选ONE|QUORUM|LOCAL_QUORUM|EACH_QUORUM|ALL|ANY|TWO|THREE|LOCAL_ONE
     */
    private String consistancyLevel = "LOCAL_QUORUM";

    private String minToken;
    private String maxToken;
}
