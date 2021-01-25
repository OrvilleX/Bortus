package com.orvillex.bortus.datapump.executor.cassandra.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.orvillex.bortus.datapump.core.element.Column;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordReceiver;
import com.orvillex.bortus.datapump.core.task.WriterTask;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.job.log.JobLogger;

/**
 * Cassandra数据库写入
 * 
 * @author y-z-f
 * @version 0.1
 */
public class CassandraWriter extends WriterTask {
    public static final String WRITE_TIME = "writetime(";

    private WriterParam writerParam;
    private Cluster cluster = null;
    private Session session = null;
    private PreparedStatement statement = null;
    private int columnNumber = 0;
    private List<DataType> columnTypes;
    private List<String> columnMeta = null;
    private int writeTimeCol = -1;
    private boolean asyncWrite = false;
    private long batchSize = 1;
    private List<ResultSetFuture> unConfirmedWrite;
    private List<BoundStatement> bufferedWrite;

    @Override
    public void startWrite(RecordReceiver lineReceiver) {
        try {
            Record record;
            while ((record = lineReceiver.getFromReader()) != null) {
                if (record.getColumnNumber() != columnNumber) {
                    throw new DataPumpException(
                            String.format("列配置信息有错误. 因为您配置的任务中，源头读取字段数:%s 与 目的表要写入的字段数:%s 不相等. 请检查您的配置并作出修改.",
                                    record.getColumnNumber(), this.columnNumber));
                }
                BoundStatement boundStmt = statement.bind();
                for (int i = 0; i < columnNumber; i++) {
                    if (writeTimeCol != -1 && i == writeTimeCol) {
                        continue;
                    }
                    Column col = record.getColumn(i);
                    int pos = i;
                    if (writeTimeCol != -1 && pos > writeTimeCol) {
                        pos = i - 1;
                    }
                    CassandraWriterHelper.setupColumn(boundStmt, pos, columnTypes.get(pos), col);
                }
                if (writeTimeCol != -1) {
                    Column col = record.getColumn(writeTimeCol);
                    boundStmt.setLong(columnNumber - 1, col.asLong());
                }
                if (batchSize <= 1) {
                    session.execute(boundStmt);
                } else {
                    if (asyncWrite) {
                        unConfirmedWrite.add(session.executeAsync(boundStmt));
                        if (unConfirmedWrite.size() >= batchSize) {
                            for (ResultSetFuture write : unConfirmedWrite) {
                                write.getUninterruptibly(10000, TimeUnit.MILLISECONDS);
                            }
                            unConfirmedWrite.clear();
                        }
                    } else {
                        bufferedWrite.add(boundStmt);
                        if (bufferedWrite.size() >= batchSize) {
                            BatchStatement batchStatement = new BatchStatement(BatchStatement.Type.UNLOGGED);
                            batchStatement.addAll(bufferedWrite);
                            try {
                                session.execute(batchStatement);
                            } catch (Exception e) {
                                JobLogger.log("batch写入失败，尝试逐条写入.", e);
                                for (BoundStatement stmt : bufferedWrite) {
                                    session.execute(stmt);
                                }
                            }
                            bufferedWrite.clear();
                        }
                    }
                }
            }
            if (unConfirmedWrite != null && unConfirmedWrite.size() > 0) {
                for (ResultSetFuture write : unConfirmedWrite) {
                    write.getUninterruptibly(10000, TimeUnit.MILLISECONDS);
                }
                unConfirmedWrite.clear();
            }
            if (bufferedWrite != null && bufferedWrite.size() > 0) {
                BatchStatement batchStatement = new BatchStatement(BatchStatement.Type.UNLOGGED);
                batchStatement.addAll(bufferedWrite);
                session.execute(batchStatement);
                bufferedWrite.clear();
            }
        } catch (Exception e) {
            throw new DataPumpException("写入数据时失败", e);
        }
    }

    @Override
    public void init() {
        writerParam = JSON.parseObject(this.getTriggerParam(), WriterParam.class);
        String username = writerParam.getUsername();
        String password = writerParam.getPassword();
        String hosts = writerParam.getHost();
        Integer port = writerParam.getPort();
        boolean useSSL = writerParam.getUseSSL();
        String keyspace = writerParam.getKeySpace();
        String table = writerParam.getTable();
        batchSize = writerParam.getBatchSize();
        this.columnMeta = writerParam.getColumn();
        columnTypes = new ArrayList<DataType>(columnMeta.size());
        columnNumber = columnMeta.size();
        asyncWrite = writerParam.isAsyncWrite();

        int connectionsPerHost = writerParam.getConnectionsPerHost();
        int maxPendingPerConnection = writerParam.getMaxPendingPreConnection();
        PoolingOptions poolingOpts = new PoolingOptions()
                .setConnectionsPerHost(HostDistance.LOCAL, connectionsPerHost, connectionsPerHost)
                .setMaxRequestsPerConnection(HostDistance.LOCAL, maxPendingPerConnection)
                .setNewConnectionThreshold(HostDistance.LOCAL, 100);
        Cluster.Builder clusterBuilder = Cluster.builder().withPoolingOptions(poolingOpts);
        if ((username != null) && !username.isEmpty()) {
            clusterBuilder = clusterBuilder.withCredentials(username, password).withPort(Integer.valueOf(port))
                    .addContactPoints(hosts.split(","));
            if (useSSL) {
                clusterBuilder = clusterBuilder.withSSL();
            }
        } else {
            clusterBuilder = clusterBuilder.withPort(Integer.valueOf(port)).addContactPoints(hosts.split(","));
        }
        cluster = clusterBuilder.build();
        session = cluster.connect(keyspace);
        TableMetadata meta = cluster.getMetadata().getKeyspace(keyspace).getTable(table);

        Insert insertStmt = QueryBuilder.insertInto(table);
        for (String colunmnName : columnMeta) {
            if (colunmnName.toLowerCase().equals(WRITE_TIME)) {
                if (writeTimeCol != -1) {
                    throw new DataPumpException("列配置信息有错误. 只能有一个时间戳列(writetime())");
                }
                writeTimeCol = columnTypes.size();
                continue;
            }
            insertStmt.value(colunmnName, QueryBuilder.bindMarker());
            ColumnMetadata col = meta.getColumn(colunmnName);
            if (col == null) {
                throw new DataPumpException(String.format("列配置信息有错误. 表中未找到列名 '%s' .", colunmnName));
            }
            columnTypes.add(col.getType());
        }
        if (writeTimeCol != -1) {
            insertStmt.using(QueryBuilder.timestamp(QueryBuilder.bindMarker()));
        }
        String cl = writerParam.getConsistancyLevel();
        if (cl != null && !cl.isEmpty()) {
            insertStmt.setConsistencyLevel(ConsistencyLevel.valueOf(cl));
        } else {
            insertStmt.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        }
        statement = session.prepare(insertStmt);
        if (batchSize > 1) {
            if (asyncWrite) {
                unConfirmedWrite = new ArrayList<ResultSetFuture>();
            } else {
                bufferedWrite = new ArrayList<BoundStatement>();
            }
        }
    }

    @Override
    public void destroy() {
    }
}
