package com.orvillex.bortus.datapump.executor.hbase.writer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.job.log.JobLogger;

public class HBaseWriterHelper {
    /**
     * phoenix瘦客户端连接前缀
     */
    public static final String CONNECT_STRING_PREFIX = "jdbc:phoenix:thin:";
    /**
     * phoenix驱动名
     */
    public static final String CONNECT_DRIVER_STRING = "org.apache.phoenix.queryserver.client.Driver";
    /**
     * 从系统表查找配置表信息
     */
    public static final String SELECT_CATALOG_TABLE_STRING = "SELECT COLUMN_NAME FROM SYSTEM.CATALOG WHERE TABLE_NAME='%s' AND COLUMN_NAME IS NOT NULL";

    /**
     * 验证配置参数是否正确
     */
    public static void validateParameter(WriterParam writerParam) {
        String tableName = writerParam.getTable();
        String queryServerAddress = writerParam.getQueryServerAddress();
        String serialization = writerParam.getSerialization();
        String connStr = getConnectionUrl(queryServerAddress, serialization);
        Connection conn = getThinClientConnection(connStr);

        List<String> columnNames = writerParam.getColumn();
        if (columnNames == null || columnNames.isEmpty()) {
            throw new DataPumpException("HBase的columns配置不能为空");
        }
        String schema = writerParam.getSchema();
        checkTable(conn, schema, tableName, columnNames);
    }

    /**
     * 获取JDBC连接，轻量级连接，使用完后必须显式close
     */
    public static Connection getThinClientConnection(String connStr) {
        JobLogger.log("Connecting to QueryServer [" + connStr + "] ...");
        Connection conn;
        try {
            Class.forName(CONNECT_DRIVER_STRING);
            conn = DriverManager.getConnection(connStr);
            conn.setAutoCommit(false);
        } catch (Throwable e) {
            throw new DataPumpException("无法连接QueryServer，配置不正确或服务未启动", e);
        }
        JobLogger.log("Connected to QueryServer successfully.");
        return conn;
    }

    public static Connection getJdbcConnection(WriterParam writerParam) {
        String queryServerAddress = writerParam.getQueryServerAddress();
        String serialization = writerParam.getSerialization();
        String connStr = getConnectionUrl(queryServerAddress, serialization);
        return getThinClientConnection(connStr);
    }

    public static String getConnectionUrl(String queryServerAddress, String serialization) {
        String urlFmt = CONNECT_STRING_PREFIX + "url=%s;serialization=%s";
        return String.format(urlFmt, queryServerAddress, serialization);
    }

    public static void checkTable(Connection conn, String schema, String tableName, List<String> columnNames)
            throws DataPumpException {
        String selectSystemTable = getSelectSystemSQL(schema, tableName);
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(selectSystemTable);
            List<String> allColumns = new ArrayList<String>();
            if (rs.next()) {
                allColumns.add(rs.getString(1));
            } else {
                JobLogger.log(tableName + "表不存在，请检查表名是否正确或是否已创建");
                throw new DataPumpException(tableName + "表不存在，请检查表名是否正确或是否已创建");
            }
            while (rs.next()) {
                allColumns.add(rs.getString(1));
            }
            for (String columnName : columnNames) {
                if (!allColumns.contains(columnName)) {
                    throw new DataPumpException("您配置的列" + columnName + "在目的表" + tableName + "的元数据中不存在");
                }
            }
        } catch (SQLException e) {
            throw new DataPumpException("获取表" + tableName + "信息失败，请检查您的集群和表状态", e);
        } finally {
            closeJdbc(conn, st, rs);
        }
    }

    private static String getSelectSystemSQL(String schema, String tableName) {
        String sql = String.format(SELECT_CATALOG_TABLE_STRING, tableName);
        if (schema != null) {
            sql = sql + " AND TABLE_SCHEM = '" + schema + "'";
        }
        return sql;
    }

    public static void closeJdbc(Connection connection, Statement statement, ResultSet resultSet) {
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
        }
    }
}
