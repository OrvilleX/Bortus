package com.orvillex.bortus.datapump.executor.hbase.writer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.orvillex.bortus.datapump.core.collector.TaskCollector;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.hbase.Constant;
import com.orvillex.bortus.job.log.JobLogger;

public class HBaseWriterTask {
    private WriterParam writerParam;
    private TaskCollector taskCollector;

    private Connection connection = null;
    private PreparedStatement pstmt = null;

    private int numberOfColumnsToWrite;
    private int numberOfColumnsToRead;
    private int[] columnTypes;
    private List<String> columns;
    private String fullTableName;

    private NullModeType nullModeType;
    private int batchSize;

    public HBaseWriterTask(WriterParam writerParam) {
        this.writerParam = writerParam;
    }

    public void startWriter(RecordReceiver lineReceiver, TaskCollector taskCollector) {
        this.taskCollector = taskCollector;
        try {
            initialize();
            writeData(lineReceiver);

        } catch (Throwable e) {
            throw new DataPumpException("写入hbase时发生IO异常", e);
        } finally {
            HBaseWriterHelper.closeJdbc(connection, pstmt, null);
        }
    }

    /**
     * 初始化JDBC操作对象及列类型
     */
    private void initialize() throws SQLException {
        if (connection == null) {
            connection = HBaseWriterHelper.getJdbcConnection(this.writerParam);
            connection.setAutoCommit(false);
        }
        nullModeType = NullModeType.getByTypeName(writerParam.getNullMode());
        batchSize = writerParam.getBatchSize();
        String schema = writerParam.getSchema();
        String tableName = writerParam.getTable();
        fullTableName = "\"" + tableName + "\"";
        if (schema != null && !schema.isEmpty()) {
            fullTableName = "\"" + schema + "\".\"" + tableName + "\"";
        }
        columns = writerParam.getColumn();
        if (pstmt == null) {
            pstmt = createPreparedStatement();
            columnTypes = getColumnSqlType();
        }
    }

    /**
     * 生成sql模板，并根据模板创建PreparedStatement
     */
    private PreparedStatement createPreparedStatement() throws SQLException {
        StringBuilder columnNamesBuilder = new StringBuilder();
        for (String col : columns) {
            columnNamesBuilder.append("\"");
            columnNamesBuilder.append(col);
            columnNamesBuilder.append("\"");
            columnNamesBuilder.append(",");
        }
        columnNamesBuilder.setLength(columnNamesBuilder.length() - 1);
        String columnNames = columnNamesBuilder.toString();
        numberOfColumnsToWrite = columns.size();
        numberOfColumnsToRead = numberOfColumnsToWrite;

        // 生成UPSERT模板
        StringBuilder upsertBuilder =
                new StringBuilder("upsert into " + fullTableName + " (" + columnNames + " ) values (");
        for (int i = 0; i < numberOfColumnsToWrite; i++) {
            upsertBuilder.append("?,");
        }
        upsertBuilder.setLength(upsertBuilder.length() - 1);
        upsertBuilder.append(")");

        String sql = upsertBuilder.toString();
        PreparedStatement ps = connection.prepareStatement(sql);
        JobLogger.log("SQL template generated: " + sql);
        return ps;
    }

    /**
     * 根据列名来从数据库元数据中获取这一列对应的SQL类型
     */
    private int[] getColumnSqlType() throws SQLException {
        int[] types = new int[numberOfColumnsToWrite];
        StringBuilder columnNamesBuilder = new StringBuilder();
        for (String columnName : columns) {
            columnNamesBuilder.append("\"").append(columnName).append("\",");
        }
        columnNamesBuilder.setLength(columnNamesBuilder.length() - 1);
        String selectSql = "SELECT " + columnNamesBuilder + " FROM " + fullTableName + " LIMIT 1";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSetMetaData meta = statement.executeQuery(selectSql).getMetaData();
            for (int i = 0; i < columns.size(); i++) {
                String name = columns.get(i);
                types[i] = meta.getColumnType(i + 1);
                JobLogger.log("Column name : " + name + ", sql type = " + types[i] + " " + meta.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            throw new DataPumpException("获取表" + fullTableName + "列类型失败", e);
        } finally {
            HBaseWriterHelper.closeJdbc(null, statement, null);
        }
        return types;
    }

    /**
     * 从接收器中获取每条记录，写入Phoenix
     */
    private void writeData(RecordReceiver lineReceiver) throws SQLException {
        List<Record> buffer = Lists.newArrayListWithExpectedSize(batchSize);
        Record record = null;
        while ((record = lineReceiver.getFromReader()) != null) {
            if (record.getColumnNumber() != numberOfColumnsToRead) {
                throw new DataPumpException("数据源给出的列数量[" + record.getColumnNumber() + "]与您配置中的列数量[" + numberOfColumnsToRead +
                "]不同");
            }
            buffer.add(record);
            if (buffer.size() > batchSize) {
                doBatchUpsert(buffer);
                buffer.clear();
            }
        }
        if (!buffer.isEmpty()) {
            doBatchUpsert(buffer);
            buffer.clear();
        }
    }

    /**
     * 批量提交一组数据，如果失败，则尝试一行行提交，如果仍然失败，抛错给用户
     */
    private void doBatchUpsert(List<Record> records) throws SQLException {
        try {
            for (Record r : records) {
                setupStatement(r);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
            pstmt.clearParameters();
            pstmt.clearBatch();

        } catch (SQLException e) {
            JobLogger.log("Failed batch committing " + records.size() + " records");
            JobLogger.log(e);

            connection.rollback();
            HBaseWriterHelper.closeJdbc(null, pstmt, null);
            connection.setAutoCommit(true);
            pstmt = createPreparedStatement();
            doSingleUpsert(records);
        } catch (Exception e) {
            throw new DataPumpException("写入hbase时发生IO异常", e);
        }
    }

    /**
     * 单行提交，将出错的行记录到脏数据中。由脏数据收集模块判断任务是否继续
     */
    private void doSingleUpsert(List<Record> records) throws SQLException {
        int rowNumber = 0;
        for (Record r : records) {
            try {
                rowNumber ++;
                setupStatement(r);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                JobLogger.log("Failed writing to phoenix, rowNumber: " + rowNumber);
                this.taskCollector.collectDirtyRecord(r, e);
            }
        }
    }

    private void setupStatement(Record record) throws SQLException {
        for (int i = 0; i < numberOfColumnsToWrite; i++) {
            Column col = record.getColumn(i);
            int sqlType = columnTypes[i];
            setupColumn(i + 1, sqlType, col);
        }
    }

    private void setupColumn(int pos, int sqlType, Column col) throws SQLException {
        if (col.getRawData() != null) {
            switch (sqlType) {
                case Types.CHAR:
                case Types.VARCHAR:
                    pstmt.setString(pos, col.asString());
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                    pstmt.setBytes(pos, col.asBytes());
                    break;
                case Types.BOOLEAN:
                    pstmt.setBoolean(pos, col.asBoolean());
                    break;
                case Types.TINYINT:
                case Constant.TYPE_UNSIGNED_TINYINT:
                    pstmt.setByte(pos, col.asLong().byteValue());
                    break;
                case Types.SMALLINT:
                case Constant.TYPE_UNSIGNED_SMALLINT:
                    pstmt.setShort(pos, col.asLong().shortValue());
                    break;
                case Types.INTEGER:
                case Constant.TYPE_UNSIGNED_INTEGER:
                    pstmt.setInt(pos, col.asLong().intValue());
                    break;
                case Types.BIGINT:
                case Constant.TYPE_UNSIGNED_LONG:
                    pstmt.setLong(pos, col.asLong());
                    break;
                case Types.FLOAT:
                    pstmt.setFloat(pos, col.asDouble().floatValue());
                    break;
                case Types.DOUBLE:
                    pstmt.setDouble(pos, col.asDouble());
                    break;
                case Types.DECIMAL:
                    pstmt.setBigDecimal(pos, col.asBigDecimal());
                    break;
                case Types.DATE:
                case Constant.TYPE_UNSIGNED_DATE:
                    pstmt.setDate(pos, new Date(col.asDate().getTime()));
                    break;
                case Types.TIME:
                case Constant.TYPE_UNSIGNED_TIME:
                    pstmt.setTime(pos, new Time(col.asDate().getTime()));
                    break;
                case Types.TIMESTAMP:
                case Constant.TYPE_UNSIGNED_TIMESTAMP:
                    pstmt.setTimestamp(pos, new Timestamp(col.asDate().getTime()));
                    break;
                default:
                    throw new DataPumpException("不支持您配置的列类型:" + sqlType);
            }
        } else {
            switch (nullModeType){
                case Skip:
                    pstmt.setNull(pos, sqlType);
                    break;
                case Empty:
                    pstmt.setObject(pos, getEmptyValue(sqlType));
                    break;
                default:
                    throw new DataPumpException("Hbasewriter 不支持该 nullMode 类型: " + nullModeType);
            }
        }
    }

    /**
     * 根据类型获取"空值"
     * 值类型的空值都是0，bool是false，String是空字符串
     * @param sqlType sql数据类型，定义于{@link Types}
     */
    private Object getEmptyValue(int sqlType) {
        switch (sqlType) {
            case Types.VARCHAR:
                return "";
            case Types.BOOLEAN:
                return false;
            case Types.TINYINT:
            case Constant.TYPE_UNSIGNED_TINYINT:
                return (byte) 0;
            case Types.SMALLINT:
            case Constant.TYPE_UNSIGNED_SMALLINT:
                return (short) 0;
            case Types.INTEGER:
            case Constant.TYPE_UNSIGNED_INTEGER:
                return (int) 0;
            case Types.BIGINT:
            case Constant.TYPE_UNSIGNED_LONG:
                return (long) 0;
            case Types.FLOAT:
                return (float) 0.0;
            case Types.DOUBLE:
                return (double) 0.0;
            case Types.DECIMAL:
                return new BigDecimal(0);
            case Types.DATE:
            case Constant.TYPE_UNSIGNED_DATE:
                return new Date(0);
            case Types.TIME:
            case Constant.TYPE_UNSIGNED_TIME:
                return new Time(0);
            case Types.TIMESTAMP:
            case Constant.TYPE_UNSIGNED_TIMESTAMP:
                return new Timestamp(0);
            case Types.BINARY:
            case Types.VARBINARY:
                return new byte[0];
            default:
                throw new DataPumpException("不支持您配置的列类型:" + sqlType);
        }
    }
}
