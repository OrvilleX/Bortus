package com.orvillex.bortus.datapump.executor.hbase.reader;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.statistics.PerfRecord;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.job.log.JobLogger;

public class HBaseReaderTask {
    private ReaderParam readerConfig;

    public HBaseReaderTask(ReaderParam config) {
        this.readerConfig = config;
    }

    public void readRecord(RecordSender recordSender) {
        String querySql = readerConfig.getQuerySql();
        JobLogger.log("Begin to read record by Sql: [{}\n] {}.", querySql);
        HBaseReaderHelper helper = new HBaseReaderHelper(readerConfig);
        Connection conn = helper.getConnection(readerConfig.getQueryServerAddress(), readerConfig.getSerialization());
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            long rsNextUsedTime = 0;
            long lastTime = System.nanoTime();
            statement = conn.createStatement();
            PerfRecord queryPerfRecord = new PerfRecord(Thread.currentThread().getName(), Phase.SQL_QUERY);
            queryPerfRecord.start();
            resultSet = statement.executeQuery(querySql);
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnNum = meta.getColumnCount();
            PerfRecord allResultPerfRecord = new PerfRecord(Thread.currentThread().getName(), Phase.RESULT_NEXT_ALL);
            allResultPerfRecord.start();

            while (resultSet.next()) {
                Record record = recordSender.createRecord();
                rsNextUsedTime += (System.nanoTime() - lastTime);
                for (int i = 1; i <= columnNum; i++) {
                    Column column = this.convertPhoenixValueToDataxColumn(meta.getColumnType(i),
                            resultSet.getObject(i));
                    record.addColumn(column);
                }
                lastTime = System.nanoTime();
                recordSender.sendToWriter(record);
            }
            allResultPerfRecord.end(rsNextUsedTime);
            JobLogger.log("Finished read record by Sql: [{}\n] {}.", querySql);
        } catch (SQLException e) {
            throw new DataPumpException("查询Phoenix数据出现异常", e);
        } finally {
            helper.closeJdbc(conn, statement, resultSet);
        }
    }

    private Column convertPhoenixValueToDataxColumn(int sqlType, Object value) {
        Column column;
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
                column = new StringColumn((String) value);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                column = new BytesColumn((byte[]) value);
                break;
            case Types.BOOLEAN:
                column = new BoolColumn((Boolean) value);
                break;
            case Types.INTEGER:
                column = new LongColumn((Integer) value);
                break;
            case Types.TINYINT:
                column = new LongColumn(((Byte) value).longValue());
                break;
            case Types.SMALLINT:
                column = new LongColumn(((Short) value).longValue());
                break;
            case Types.BIGINT:
                column = new LongColumn((Long) value);
                break;
            case Types.FLOAT:
                column = new DoubleColumn((Float.valueOf(value.toString())));
                break;
            case Types.DECIMAL:
                column = new DoubleColumn((BigDecimal) value);
                break;
            case Types.DOUBLE:
                column = new DoubleColumn((Double) value);
                break;
            case Types.DATE:
                column = new DateColumn((Date) value);
                break;
            case Types.TIME:
                column = new DateColumn((Time) value);
                break;
            case Types.TIMESTAMP:
                column = new DateColumn((Timestamp) value);
                break;
            default:
                throw new DataPumpException("遇到不可识别的phoenix类型，" + "sqlType :" + sqlType);
        }
        return column;
    }
}
