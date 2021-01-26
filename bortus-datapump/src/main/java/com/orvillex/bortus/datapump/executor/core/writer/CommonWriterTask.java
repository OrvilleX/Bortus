package com.orvillex.bortus.datapump.executor.core.writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.WriteConcern;
import com.orvillex.bortus.datapump.core.collector.TaskCollector;
import com.orvillex.bortus.datapump.core.element.*;
import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.core.util.Constant;
import com.orvillex.bortus.datapump.executor.core.util.DBUtil;
import com.orvillex.bortus.datapump.executor.core.util.DataBaseType;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

public class CommonWriterTask {
    protected DataBaseType dataBaseType;
    private static final String VALUE_HOLDER = "?";

    protected String username;
    protected String password;
    protected String jdbcUrl;
    protected String table;
    protected List<String> columns;
    protected int batchSize;
    protected int batchByteSize;
    protected int columnNumber = 0;
    protected TaskCollector taskCollector;

    protected static String BASIC_MESSAGE;
    protected static String INSERT_OR_REPLACE_TEMPLATE;

    protected String writeRecordSql;
    protected String writeMode;
    protected boolean emptyAsNull;
    protected Triple<List<String>, List<Integer>, List<String>> resultSetMetaData;

    public CommonWriterTask(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public void init(CommonWriterParam writerConfig) {
        this.username = writerConfig.getUsername();
        this.password = writerConfig.getPassword();
        this.jdbcUrl = writerConfig.getJbdc();

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

        this.table = writerConfig.getTable();
        this.columns = writerConfig.getColumn();
        this.columnNumber = this.columns.size();
        this.batchSize = writerConfig.getBatchSize();
        this.batchByteSize = writerConfig.getBatchByteSize();
        writeMode = writerConfig.getWriteMode();
        emptyAsNull = writerConfig.getEmptyAsNull();
        INSERT_OR_REPLACE_TEMPLATE = writerConfig.getInsertOrReplaceTemplate();
        this.writeRecordSql = String.format(INSERT_OR_REPLACE_TEMPLATE, this.table);
        BASIC_MESSAGE = String.format("jdbcUrl:[%s], table:[%s]", this.jdbcUrl, this.table);
    }

    public void startWriteWithConnection(RecordReceiver recordReceiver, TaskCollector taskCollector,
            Connection connection) {
        this.taskCollector = taskCollector;
        this.resultSetMetaData = DBUtil.getColumnMetaData(connection, this.table, StringUtils.join(this.columns, ","));
        calcWriteRecordSql();

        List<Record> writeBuffer = new ArrayList<Record>(this.batchSize);
        int bufferBytes = 0;
        try {
            Record record;
            while ((record = recordReceiver.getFromReader()) != null) {
                if (record.getColumnNumber() != this.columnNumber) {
                    throw new DataPumpException(
                            String.format("列配置信息有错误. 因为您配置的任务中，源头读取字段数:%s 与 目的表要写入的字段数:%s 不相等. 请检查您的配置并作出修改.",
                                    record.getColumnNumber(), this.columnNumber));
                }
                writeBuffer.add(record);
                bufferBytes += record.getMemorySize();
                if (writeBuffer.size() >= batchSize || bufferBytes >= batchByteSize) {
                    doBatchInsert(connection, writeBuffer);
                    writeBuffer.clear();
                    bufferBytes = 0;
                }
            }
            if (!writeBuffer.isEmpty()) {
                doBatchInsert(connection, writeBuffer);
                writeBuffer.clear();
                bufferBytes = 0;
            }
        } catch (Exception e) {
            throw new DataPumpException("写入表中写入数据时失败", e);
        } finally {
            writeBuffer.clear();
            bufferBytes = 0;
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    public void startWrite(RecordReceiver recordReceiver, CommonWriterParam writerConfig, TaskCollector taskCollector) {
        Connection connection = DBUtil.getConnection(this.dataBaseType, this.jdbcUrl, username, password);
        DBUtil.dealWithSessionConfig(connection, writerConfig, this.dataBaseType, BASIC_MESSAGE);
        startWriteWithConnection(recordReceiver, taskCollector, connection);
    }

    protected void doBatchInsert(Connection connection, List<Record> buffer) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(this.writeRecordSql);
            for (Record record : buffer) {
                preparedStatement = fillPreparedStatement(preparedStatement, record);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            JobLogger.log("回滚此次写入, 采用每次写入一行方式提交. 因为:" + e.getMessage());
            connection.rollback();
            doOneInsert(connection, buffer);
        } catch (Exception e) {
            throw new DataPumpException("写入表中写入数据时失败", e);
        } finally {
            DBUtil.closeDBResources(preparedStatement, null);
        }
    }

    protected void doOneInsert(Connection connection, List<Record> buffer) {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(true);
            preparedStatement = connection.prepareStatement(this.writeRecordSql);

            for (Record record : buffer) {
                try {
                    preparedStatement = fillPreparedStatement(preparedStatement, record);
                    preparedStatement.execute();
                } catch (SQLException e) {
                    JobLogger.log(e.toString());
                    this.taskCollector.collectDirtyRecord(record, e);
                } finally {
                    preparedStatement.clearParameters();
                }
            }
        } catch (Exception e) {
            throw new DataPumpException("写入表中写入数据时失败", e);
        } finally {
            DBUtil.closeDBResources(preparedStatement, null);
        }
    }

    protected PreparedStatement fillPreparedStatement(PreparedStatement preparedStatement, Record record)
            throws SQLException {
        for (int i = 0; i < this.columnNumber; i++) {
            int columnSqltype = this.resultSetMetaData.getMiddle().get(i);
            preparedStatement = fillPreparedStatementColumnType(preparedStatement, i, columnSqltype,
                    record.getColumn(i));
        }
        return preparedStatement;
    }

    protected PreparedStatement fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex,
            int columnSqltype, Column column) throws SQLException {
        java.util.Date utilDate;
        switch (columnSqltype) {
            case Types.CHAR:
            case Types.NCHAR:
            case Types.CLOB:
            case Types.NCLOB:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                preparedStatement.setString(columnIndex + 1, column.asString());
                break;
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                String strValue = column.asString();
                if (emptyAsNull && "".equals(strValue)) {
                    preparedStatement.setString(columnIndex + 1, null);
                } else {
                    preparedStatement.setString(columnIndex + 1, strValue);
                }
                break;
            case Types.TINYINT:
                Long longValue = column.asLong();
                if (null == longValue) {
                    preparedStatement.setString(columnIndex + 1, null);
                } else {
                    preparedStatement.setString(columnIndex + 1, longValue.toString());
                }
                break;
            case Types.DATE:
                if (this.resultSetMetaData.getRight().get(columnIndex).equalsIgnoreCase("year")) {
                    if (column.asBigInteger() == null) {
                        preparedStatement.setString(columnIndex + 1, null);
                    } else {
                        preparedStatement.setInt(columnIndex + 1, column.asBigInteger().intValue());
                    }
                } else {
                    java.sql.Date sqlDate = null;
                    try {
                        utilDate = column.asDate();
                    } catch (DataPumpException e) {
                        throw new SQLException(String.format("Date 类型转换错误：[%s]", column));
                    }
                    if (null != utilDate) {
                        sqlDate = new java.sql.Date(utilDate.getTime());
                    }
                    preparedStatement.setDate(columnIndex + 1, sqlDate);
                }
                break;
            case Types.TIME:
                java.sql.Time sqlTime = null;
                try {
                    utilDate = column.asDate();
                } catch (DataPumpException e) {
                    throw new SQLException(String.format("TIME 类型转换错误：[%s]", column));
                }
                if (null != utilDate) {
                    sqlTime = new java.sql.Time(utilDate.getTime());
                }
                preparedStatement.setTime(columnIndex + 1, sqlTime);
                break;
            case Types.TIMESTAMP:
                java.sql.Timestamp sqlTimestamp = null;
                try {
                    utilDate = column.asDate();
                } catch (DataPumpException e) {
                    throw new SQLException(String.format("TIMESTAMP 类型转换错误：[%s]", column));
                }
                if (null != utilDate) {
                    sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
                }
                preparedStatement.setTimestamp(columnIndex + 1, sqlTimestamp);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.BLOB:
            case Types.LONGVARBINARY:
                preparedStatement.setBytes(columnIndex + 1, column.asBytes());
                break;
            case Types.BOOLEAN:
                preparedStatement.setString(columnIndex + 1, column.asString());
                break;
            case Types.BIT:
                if (this.dataBaseType == DataBaseType.MySql) {
                    preparedStatement.setBoolean(columnIndex + 1, column.asBoolean());
                } else {
                    preparedStatement.setString(columnIndex + 1, column.asString());
                }
                break;
            default:
                throw new DataPumpException(String.format(
                        "您的配置文件中的列配置信息有误. 因为DataX 不支持数据库写入这种字段类型. 字段名:[%s], 字段类型:[%d], 字段Java类型:[%s]. 请修改表中该字段的类型或者不同步该字段.",
                        this.resultSetMetaData.getLeft().get(columnIndex),
                        this.resultSetMetaData.getMiddle().get(columnIndex),
                        this.resultSetMetaData.getRight().get(columnIndex)));
        }
        return preparedStatement;
    }

    private void calcWriteRecordSql() {
        if (!VALUE_HOLDER.equals(calcValueHolder(""))) {
            List<String> valueHolders = new ArrayList<String>(columnNumber);
            for (int i = 0; i < columns.size(); i++) {
                String type = resultSetMetaData.getRight().get(i);
                valueHolders.add(calcValueHolder(type));
            }
            boolean forceUseUpdate = false;
            if (dataBaseType != null && dataBaseType == DataBaseType.MySql && isOB10(jdbcUrl)) {
                forceUseUpdate = true;
            }
            INSERT_OR_REPLACE_TEMPLATE = getWriteTemplate(columns, valueHolders, writeMode, dataBaseType,
                    forceUseUpdate);
            writeRecordSql = String.format(INSERT_OR_REPLACE_TEMPLATE, this.table);
        }
    }

    public static boolean isOB10(String jdbcUrl) {
        if (jdbcUrl.startsWith(Constant.OB10_SPLIT_STRING)) {
            String[] ss = jdbcUrl.split(Constant.OB10_SPLIT_STRING_PATTERN);
            if (ss.length != 3) {
                throw new DataPumpException("JDBC OB10格式错误");
            }
            return true;
        }
        return false;
    }

    protected String calcValueHolder(String columnType) {
        return VALUE_HOLDER;
    }

    private String getWriteTemplate(List<String> columnHolders, List<String> valueHolders, String writeMode, DataBaseType dataBaseType, 
        boolean forceUseUpdate) {
        boolean isWriteModeLegal = writeMode.trim().toLowerCase().startsWith("insert")
                || writeMode.trim().toLowerCase().startsWith("replace")
                || writeMode.trim().toLowerCase().startsWith("update");

        if (!isWriteModeLegal) {
            throw new DataPumpException(String.format(
                    "您所配置的 writeMode:%s 错误. 因为DataX 目前仅支持replace,update 或 insert 方式. 请检查您的配置并作出修改.", writeMode));
        }
        String writeDataSqlTemplate;
        if (forceUseUpdate || ((dataBaseType == DataBaseType.MySql)
                && writeMode.trim().toLowerCase().startsWith("update"))) {
            writeDataSqlTemplate = new StringBuilder().append("INSERT INTO %s (")
                    .append(StringUtils.join(columnHolders, ",")).append(") VALUES(")
                    .append(StringUtils.join(valueHolders, ",")).append(")")
                    .append(onDuplicateKeyUpdateString(columnHolders)).toString();
        } else {
            if (writeMode.trim().toLowerCase().startsWith("update")) {
                writeMode = "replace";
            }
            writeDataSqlTemplate = new StringBuilder().append(writeMode).append(" INTO %s (")
                    .append(StringUtils.join(columnHolders, ",")).append(") VALUES(")
                    .append(StringUtils.join(valueHolders, ",")).append(")").toString();
        }
        return writeDataSqlTemplate;
    }

    private String onDuplicateKeyUpdateString(List<String> columnHolders) {
        if (columnHolders == null || columnHolders.size() < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ON DUPLICATE KEY UPDATE ");
        boolean first = true;
        for (String column : columnHolders) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(column);
            sb.append("=VALUES(");
            sb.append(column);
            sb.append(")");
        }
        return sb.toString();
    }
}
