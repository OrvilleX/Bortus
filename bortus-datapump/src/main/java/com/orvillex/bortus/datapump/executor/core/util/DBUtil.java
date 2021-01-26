package com.orvillex.bortus.datapump.executor.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import com.orvillex.bortus.datapump.exception.DataPumpException;
import com.orvillex.bortus.datapump.executor.core.CommonParam;
import com.orvillex.bortus.datapump.executor.core.reader.CommonReaderParam;
import com.orvillex.bortus.datapump.utils.RetryUtil;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public final class DBUtil {
    public static Connection getConnection(final DataBaseType dataBaseType, final String jdbcUrl, final String username,
            final String password) {
        return getConnection(dataBaseType, jdbcUrl, username, password,
                String.valueOf(Constant.SOCKET_TIMEOUT_INSECOND * 1000));
    }

    public static Connection getConnection(final DataBaseType dataBaseType, final String jdbcUrl, final String username,
            final String password, final String socketTimeout) {

        try {
            return RetryUtil.executeWithRetry(new Callable<Connection>() {
                @Override
                public Connection call() throws Exception {
                    return DBUtil.connect(dataBaseType, jdbcUrl, username, password, socketTimeout);
                }
            }, 9, 1000L, true);
        } catch (Exception e) {
            throw new DataPumpException(String.format("数据库连接失败. 因为根据您配置的连接信息:%s获取数据库连接失败. 请检查您的配置并作出修改.", jdbcUrl), e);
        }
    }

    private static synchronized Connection connect(DataBaseType dataBaseType, String url, String user, String pass,
            String socketTimeout) {

        if (url.startsWith(Constant.OB10_SPLIT_STRING) && dataBaseType == DataBaseType.MySql) {
            String[] ss = url.split(Constant.OB10_SPLIT_STRING_PATTERN);
            if (ss.length != 3) {
                throw new DataPumpException("JDBC OB10格式错误");
            }
            JobLogger.log("this is ob1_0 jdbc url.");
            user = ss[1].trim() + ":" + user;
            url = ss[2];
            JobLogger.log("this is ob1_0 jdbc url. user=" + user + " :url=" + url);
        }
        Properties prop = new Properties();
        prop.put("user", user);
        prop.put("password", pass);
        if (dataBaseType == DataBaseType.Oracle) {
            prop.put("oracle.jdbc.ReadTimeout", socketTimeout);
        }
        return connect(dataBaseType, url, prop);
    }

    private static synchronized Connection connect(DataBaseType dataBaseType, String url, Properties prop) {
        try {
            Class.forName(dataBaseType.getDriverClassName());
            DriverManager.setLoginTimeout(Constant.TIMEOUT_SECONDS);
            return DriverManager.getConnection(url, prop);
        } catch (Exception e) {
            throw RDBMSException.asConnException(dataBaseType, e, prop.getProperty("user"), null);
        }
    }

    public static void dealWithSessionConfig(Connection conn, CommonParam config, DataBaseType databaseType,
            String message) {
        List<String> sessionConfig = null;
        switch (databaseType) {
            case Oracle:
                sessionConfig = config.getSession();
                DBUtil.doDealWithSessionConfig(conn, sessionConfig, message);
                break;
            case MySql:
                sessionConfig = config.getSession();
                DBUtil.doDealWithSessionConfig(conn, sessionConfig, message);
                break;
            default:
                break;
        }
    }

    private static void doDealWithSessionConfig(Connection conn, List<String> sessions, String message) {
        if (null == sessions || sessions.isEmpty()) {
            return;
        }
        Statement stmt;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new DataPumpException(
                    String.format("session配置有误. 因为根据您的配置执行 session 设置失败. 上下文信息是:[%s]. 请检查您的配置并作出修改.", message), e);
        }

        for (String sessionSql : sessions) {
            JobLogger.log("execute sql:[{}]", sessionSql);
            try {
                DBUtil.executeSqlWithoutResultSet(stmt, sessionSql);
            } catch (SQLException e) {
                throw new DataPumpException(
                        String.format("session配置有误. 因为根据您的配置执行 session 设置失败. 上下文信息是:[%s]. 请检查您的配置并作出修改.", message), e);
            }
        }
        DBUtil.closeDBResources(stmt, null);
    }

    public static void executeSqlWithoutResultSet(Statement stmt, String sql) throws SQLException {
        stmt.execute(sql);
    }

    public static void closeDBResources(Statement stmt, Connection conn) {
        closeDBResources(null, stmt, conn);
    }

    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException unused) {
            }
        }
        if (null != stmt) {
            try {
                stmt.close();
            } catch (SQLException unused) {
            }
        }
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException unused) {
            }
        }
    }

    public static ResultSet query(Connection conn, String sql, int fetchSize) throws SQLException {
        return query(conn, sql, fetchSize, Constant.SOCKET_TIMEOUT_INSECOND);
    }

    public static ResultSet query(Connection conn, String sql, int fetchSize, int queryTimeout) throws SQLException {
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(fetchSize);
        stmt.setQueryTimeout(queryTimeout);
        return query(stmt, sql);
    }

    public static ResultSet query(Statement stmt, String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    public static Triple<List<String>, List<Integer>, List<String>> getColumnMetaData(Connection conn, String tableName,
            String column) {
        Statement statement = null;
        ResultSet rs = null;
        Triple<List<String>, List<Integer>, List<String>> columnMetaData = new ImmutableTriple<List<String>, List<Integer>, List<String>>(
                new ArrayList<String>(), new ArrayList<Integer>(), new ArrayList<String>());
        try {
            statement = conn.createStatement();
            String queryColumnSql = "select " + column + " from " + tableName + " where 1=2";
            rs = statement.executeQuery(queryColumnSql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
                columnMetaData.getLeft().add(rsMetaData.getColumnName(i + 1));
                columnMetaData.getMiddle().add(rsMetaData.getColumnType(i + 1));
                columnMetaData.getRight().add(rsMetaData.getColumnTypeName(i + 1));
            }
            return columnMetaData;
        } catch (SQLException e) {
            throw new DataPumpException(String.format("获取表:%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.", tableName), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, null);
        }
    }
}
