package com.orvillex.bortus.datapump.executor.cassandra;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.orvillex.bortus.datapump.core.element.Record;
import com.orvillex.bortus.datapump.core.element.RecordSender;
import com.orvillex.bortus.datapump.core.task.ReaderTask;
import com.orvillex.bortus.job.log.JobLogger;

/**
 * Cassandra数据库读取
 * @author y-z-f
 * @version 0.1
 */
public class CassandraReader extends ReaderTask {
    private ReaderParam readerParam;
    private Cluster cluster = null;
    private Session session = null;
    private String queryString = null;
    private ConsistencyLevel consistencyLevel;
    private int columnNumber = 0;

    @Override
    public void startRead(RecordSender recordSender) {
      ResultSet r = session.execute(new SimpleStatement(queryString).setConsistencyLevel(consistencyLevel));
      for (Row row : r ) {
        Record record = recordSender.createRecord();
        record = CassandraReaderHelper.buildRecord(record, row, r.getColumnDefinitions(), columnNumber, this.getTaskCollector());
        if( record != null )
          recordSender.sendToWriter(record);
      }
    }

    @Override
    public void init() {
        readerParam = JSON.parseObject(this.getTriggerParam(), ReaderParam.class);
        String username = readerParam.getUsername();
        String password = readerParam.getPassword();
        String hosts = readerParam.getHost();
        Integer port = readerParam.getPort();
        boolean useSSL = readerParam.getUseSSL();
        String keyspace = readerParam.getKeySpace();

        if ((username != null) && !username.isEmpty()) {
            Cluster.Builder clusterBuilder = Cluster.builder().withCredentials(username, password)
                .withPort(Integer.valueOf(port)).addContactPoints(hosts.split(","));
            if (useSSL) {
              clusterBuilder = clusterBuilder.withSSL();
            }
            cluster = clusterBuilder.build();
          } else {
            cluster = Cluster.builder().withPort(Integer.valueOf(port))
                .addContactPoints(hosts.split(",")).build();
          }
          session = cluster.connect(keyspace);
          String cl = readerParam.getConsistancyLevel();
          if( cl != null && !cl.isEmpty() ) {
            consistencyLevel = ConsistencyLevel.valueOf(cl);
          } else {
            consistencyLevel = ConsistencyLevel.LOCAL_QUORUM;
          }
          CassandraReaderHelper.checkConfig(readerParam, cluster);
    
          queryString = CassandraReaderHelper.getQueryString(readerParam, cluster);
          JobLogger.log("query = " + queryString);
    }

    @Override
    public void destroy() {
    }
}
