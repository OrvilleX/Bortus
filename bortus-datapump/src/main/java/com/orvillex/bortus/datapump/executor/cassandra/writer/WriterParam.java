package com.orvillex.bortus.datapump.executor.cassandra.writer;

import java.util.List;

import lombok.Data;

/**
 * 写入配置
 */
@Data
public class WriterParam {
    private String username;
    private String password;

    /**
     * Cassandra连接点的域名或ip，多个node之间用逗号分隔（必填）
     */
    private String host;

    /**
     * Cassandra端口（必填）
     */
    private Integer port = 9042;
    private Boolean useSSL;

    /**
     * 需要同步的表所在的keyspace（必填）
     */
    private String keySpace;

    /**
     * 所选取的需要同步的表（必填）
     */
    private String table;

    /**
     * 描述：一次批量提交(UNLOGGED BATCH)的记录数大小（条数）。注意batch的大小有如下限制：<br />
       (1）不能超过65535。<br />
       (2) batch中的内容大小受到服务器端batch_size_fail_threshold_in_kb的限制。<br />
       (3) 如果batch中的内容超过了batch_size_warn_threshold_in_kb的限制，会打出warn日志，但并不影响写入，忽略即可。<br />
       如果批量提交失败，会把这个批量的所有内容重新逐条写入一遍。
     */
    private Long batchSize = 1l;

    /**
     * 所配置的表中需要同步的列集合（必填）
     */
    private List<String> column;
    private boolean asyncWrite = false;

    /**
     * 客户端连接池配置,与服务器每个节点建多少个连接
     */
    private Integer connectionsPerHost = 8;

    /**
     * 客户端连接池配置,每个连接最大请求数
     */
    private Integer maxPendingPreConnection = 128;

    /**
     * 数据一致性级别。可选ONE|QUORUM|LOCAL_QUORUM|EACH_QUORUM|ALL|ANY|TWO|THREE|LOCAL_ONE
     */
    private String consistancyLevel = "LOCAL_QUORUM";
}
