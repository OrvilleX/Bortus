package com.orvillex.bortus.datapump.executor.hbase.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;

public class HBaseReaderHelper {
    private ReaderParam readerParam;
    private Connection connection;
    private String querySql;
    private List<String> columnNames;
    private String splitKey;

    public HBaseReaderHelper (ReaderParam readerParam) {
        this.readerParam = readerParam;
    }

    /**
     *  校验配置参数是否正确
     */
    public void validateParameter() {
        if(StringUtils.isNotBlank(readerParam.getQueryServerAddress())) {
            throw new DataPumpException("您缺失了必须填写的参数值");
        }
        String queryServerAddress = readerParam.getQueryServerAddress();
        String serialization = readerParam.getSerialization();
        connection = getConnection(queryServerAddress, serialization);
        querySql = readerParam.getQuerySql();
        if (querySql == null || querySql.isEmpty()) {
            JobLogger.log("Split according to splitKey or split points.");
            String schema = readerParam.getSchema();
            String tableName = readerParam.getTable();
            columnNames = readerParam.getColumn();
            splitKey = readerParam.getSplitKey();
            checkTable(schema, tableName);
            dealWhere();
        } else {
            JobLogger.log("Split according to query sql.");
        }
    }

    public Connection getConnection(String queryServerAddress, String serialization) {
        String url = String.format(Constant.CONNECT_STRING_TEMPLATE, queryServerAddress, serialization);
        JobLogger.log("Connecting to QueryServer [" + url + "] ...");
        Connection conn;
        try {
            Class.forName(Constant.CONNECT_DRIVER_STRING);
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
        } catch (Throwable e) {
            throw new DataPumpException("无法连接QueryServer，配置不正确或服务未启动");
        }
        JobLogger.log("Connected to QueryServer successfully.");
        return conn;
    }

    /**
     * 检查表名、列名和切分列是否存在
     */
    public void checkTable(String schema, String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            String selectSql = String.format(Constant.SELECT_COLUMNS_TEMPLATE, tableName);
            if (schema == null || schema.isEmpty()) {
                selectSql = selectSql + " AND TABLE_SCHEM IS NULL";
            } else {
                selectSql = selectSql + " AND TABLE_SCHEM = '" + schema + "'";
            }
            resultSet = statement.executeQuery(selectSql);
            List<String> primaryColumnNames = new ArrayList<String>();
            List<String> allColumnName = new ArrayList<String>();
            while (resultSet.next()) {
                String columnName = resultSet.getString(1);
                allColumnName.add(columnName);
                if (resultSet.getString(2) == null) {
                    primaryColumnNames.add(columnName);
                }
            }
            if (columnNames != null && !columnNames.isEmpty()) {
                for (String columnName : columnNames) {
                    if (!allColumnName.contains(columnName)) {
                        throw new DataPumpException("您配置的列" + columnName + "在表" + tableName + "的元数据中不存在");
                    }
                }
            } else {
                columnNames = allColumnName;
            }
            if (splitKey != null) {
                if (!primaryColumnNames.contains(splitKey)) {
                    throw new DataPumpException("您配置的切分列" + splitKey + "不是表" + tableName + "的主键");
                }
            }

        } catch (SQLException e) {
            throw new DataPumpException("获取表" + tableName + "信息失败，请检查您的集群和表状态");
        } finally {
            closeJdbc(null, statement, resultSet);
        }
    }

    public void closeJdbc(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            JobLogger.log("数据库连接关闭异常");
            JobLogger.log(e);
        }
    }

    public void dealWhere() {
        String where = readerParam.getWhere();
        if(StringUtils.isNotBlank(where)) {
            String whereImprove = where.trim();
            if(whereImprove.endsWith(";") || whereImprove.endsWith("；")) {
                whereImprove = whereImprove.substring(0,whereImprove.length()-1);
            }
        }
    }
}
