package com.orvillex.bortus.datapump.executor.core.reader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import com.orvillex.bortus.datapump.core.collector.TaskCollector;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.statistics.PerfRecord;
import com.orvillex.bortus.datapump.core.statistics.PerfTrace;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.core.util.Constant;
import com.orvillex.bortus.datapump.executor.core.util.DBUtil;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;
import com.orvillex.bortus.datapump.executor.core.util.RDBMSException;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;

public class CommonReaderTask {
    protected final byte[] EMPTY_CHAR_ARRAY = new byte[0];

    private DataBaseType dataBaseType;
    private String username;
    private String password;
    private String jdbcUrl;
    private String mandatoryEncoding;
    private String basicMsg;

    public CommonReaderTask(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public void init(CommonReaderParam readerParam) {
        this.username = readerParam.getUsername();
        this.password = readerParam.getPassword();
        this.jdbcUrl = readerParam.getJbdc();

        if (this.jdbcUrl.startsWith(Constant.OB10_SPLIT_STRING) && this.dataBaseType == DataBaseType.MySql) {
            String[] ss = this.jdbcUrl.split(Constant.OB10_SPLIT_STRING_PATTERN);
            if (ss.length != 3) {
                throw new DataPumpException("JDBC OB10格式错误");
            }
            JobLogger.log("this is ob1_0 jdbc url.");
            this.username = ss[1].trim() + ":" + this.username;
            this.jdbcUrl = ss[2];
            JobLogger.log("this is ob1_0 jdbc url. user=" + this.username + " :url=" + this.jdbcUrl);
        }
        this.mandatoryEncoding = readerParam.getMandatoryEncoding();
        basicMsg = String.format("jdbcUrl:[%s]", this.jdbcUrl);
    }

    public void startRead(CommonReaderParam readerParam, RecordSender recordSender, TaskCollector taskCollector,
            int fetchSize) {
        String querySql = readerParam.getQuerySql();
        String table = readerParam.getTable();

        PerfTrace.getInstance().addTaskDetails(Thread.currentThread().getName(), table + "," + basicMsg);
        JobLogger.log("Begin to read record by Sql: [{}\n] {}.", querySql, basicMsg);
        PerfRecord queryPerfRecord = new PerfRecord(Thread.currentThread().getName(), Phase.SQL_QUERY);
        queryPerfRecord.start();

        Connection conn = DBUtil.getConnection(this.dataBaseType, jdbcUrl, username, password);
        DBUtil.dealWithSessionConfig(conn, readerParam, this.dataBaseType, basicMsg);

        int columnNumber = 0;
        ResultSet rs = null;
        try {
            rs = DBUtil.query(conn, querySql, fetchSize);
            queryPerfRecord.end();
            ResultSetMetaData metaData = rs.getMetaData();
            columnNumber = metaData.getColumnCount();
            PerfRecord allResultPerfRecord = new PerfRecord(Thread.currentThread().getName(), Phase.RESULT_NEXT_ALL);
            allResultPerfRecord.start();
            long rsNextUsedTime = 0;
            long lastTime = System.nanoTime();
            while (rs.next()) {
                rsNextUsedTime += (System.nanoTime() - lastTime);
                this.transportOneRecord(recordSender, rs, metaData, columnNumber, mandatoryEncoding, taskCollector);
                lastTime = System.nanoTime();
            }
            allResultPerfRecord.end(rsNextUsedTime);
            JobLogger.log("Finished read record by Sql: [{}\n] {}.", querySql, basicMsg);
        } catch (Exception e) {
            throw RDBMSException.asQueryException(this.dataBaseType, e, querySql, table, username);
        } finally {
            DBUtil.closeDBResources(null, conn);
        }
    }

    protected Record transportOneRecord(RecordSender recordSender, ResultSet rs, ResultSetMetaData metaData,
            int columnNumber, String mandatoryEncoding, TaskCollector taskPluginCollector) {
        Record record = buildRecord(recordSender, rs, metaData, columnNumber, mandatoryEncoding, taskPluginCollector);
        recordSender.sendToWriter(record);
        return record;
    }

    protected Record buildRecord(RecordSender recordSender, ResultSet rs, ResultSetMetaData metaData, int columnNumber,
            String mandatoryEncoding, TaskCollector taskPluginCollector) {
        Record record = recordSender.createRecord();
        try {
            for (int i = 1; i <= columnNumber; i++) {
                switch (metaData.getColumnType(i)) {
                    case Types.CHAR:
                    case Types.NCHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGNVARCHAR:
                        String rawData;
                        if (StringUtils.isBlank(mandatoryEncoding)) {
                            rawData = rs.getString(i);
                        } else {
                            rawData = new String((rs.getBytes(i) == null ? EMPTY_CHAR_ARRAY : rs.getBytes(i)),
                                    mandatoryEncoding);
                        }
                        record.addColumn(new StringColumn(rawData));
                        break;
                    case Types.CLOB:
                    case Types.NCLOB:
                        record.addColumn(new StringColumn(rs.getString(i)));
                        break;
                    case Types.SMALLINT:
                    case Types.TINYINT:
                    case Types.INTEGER:
                    case Types.BIGINT:
                        record.addColumn(new LongColumn(rs.getString(i)));
                        break;
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                        record.addColumn(new DoubleColumn(rs.getString(i)));
                        break;
                    case Types.FLOAT:
                    case Types.REAL:
                    case Types.DOUBLE:
                        record.addColumn(new DoubleColumn(rs.getString(i)));
                        break;
                    case Types.TIME:
                        record.addColumn(new DateColumn(rs.getTime(i)));
                        break;
                    case Types.DATE:
                        if (metaData.getColumnTypeName(i).equalsIgnoreCase("year")) {
                            record.addColumn(new LongColumn(rs.getInt(i)));
                        } else {
                            record.addColumn(new DateColumn(rs.getDate(i)));
                        }
                        break;
                    case Types.TIMESTAMP:
                        record.addColumn(new DateColumn(rs.getTimestamp(i)));
                        break;
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.BLOB:
                    case Types.LONGVARBINARY:
                        record.addColumn(new BytesColumn(rs.getBytes(i)));
                        break;
                    case Types.BOOLEAN:
                    case Types.BIT:
                        record.addColumn(new BoolColumn(rs.getBoolean(i)));
                        break;
                    case Types.NULL:
                        String stringData = null;
                        if (rs.getObject(i) != null) {
                            stringData = rs.getObject(i).toString();
                        }
                        record.addColumn(new StringColumn(stringData));
                        break;
                    default:
                        throw new DataPumpException(String.format(
                                "DataPump不支持数据库读取这种字段类型, 字段名:[%s], 字段名称:[%s], 字段Java类型:[%s]. 请尝试使用数据库函数将其转换datax支持的类型 或者不同步该字段 .",
                                metaData.getColumnName(i), metaData.getColumnType(i), metaData.getColumnClassName(i)));
                }
            }
        } catch (Exception e) {
            taskPluginCollector.collectDirtyRecord(record, e);
            if (e instanceof DataPumpException) {
                throw (DataPumpException) e;
            }
        }
        return record;
    }
}
