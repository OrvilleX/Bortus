package com.orvillex.bortus.datapump.executor.core.util;

import com.orvillex.bortus.datapump.exception.DataPumpException;

public class RDBMSException extends DataPumpException {
    public RDBMSException(String message){
        super(message);
    }

    public static DataPumpException asConnException(DataBaseType dataBaseType,Exception e,String userName,String dbName){
        if (dataBaseType.equals(DataBaseType.MySql)){
            DBUtilErrorCode dbUtilErrorCode = mySqlConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_DB_ERROR && dbName !=null ) {
                return new DataPumpException("该数据库名称为："+dbName+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR ){
                return new DataPumpException("该数据库用户名为："+userName+" 具体错误信息为："+e);
            }
            return new DataPumpException("具体错误信息为："+e);
        }
        if (dataBaseType.equals(DataBaseType.Oracle)){
            DBUtilErrorCode dbUtilErrorCode = oracleConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_DB_ERROR && dbName != null){
                return new DataPumpException("该数据库名称为："+dbName+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR ){
                return new DataPumpException("该数据库用户名为："+userName+" 具体错误信息为："+e);
            }
            return new DataPumpException("具体错误信息为："+e);
        }
        return new DataPumpException("具体错误信息为："+e);
    }

    public static DBUtilErrorCode mySqlConnectionErrorAna(String e){
        if (e.contains(Constant.MYSQL_DATABASE)){
            return DBUtilErrorCode.MYSQL_CONN_DB_ERROR;
        }
        if (e.contains(Constant.MYSQL_CONNEXP)){
            return DBUtilErrorCode.MYSQL_CONN_IPPORT_ERROR;
        }
        if (e.contains(Constant.MYSQL_ACCDENIED)){
            return DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR;
        }
        return DBUtilErrorCode.CONN_DB_ERROR;
    }

    public static DBUtilErrorCode oracleConnectionErrorAna(String e){
        if (e.contains(Constant.ORACLE_DATABASE)){
            return DBUtilErrorCode.ORACLE_CONN_DB_ERROR;
        }
        if (e.contains(Constant.ORACLE_CONNEXP)){
            return DBUtilErrorCode.ORACLE_CONN_IPPORT_ERROR;
        }
        if (e.contains(Constant.ORACLE_ACCDENIED)){
            return DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR;
        }
        return DBUtilErrorCode.CONN_DB_ERROR;
    }

    public static DataPumpException asQueryException(DataBaseType dataBaseType, Exception e,String querySql,String table,String userName){
        if (dataBaseType.equals(DataBaseType.MySql)){
            DBUtilErrorCode dbUtilErrorCode = mySqlQueryErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_QUERY_TABLE_NAME_ERROR && table != null) {
                return new DataPumpException("表名为："+table+" 执行的SQL为:"+querySql+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_QUERY_SELECT_PRI_ERROR && userName != null){
                return new DataPumpException("用户名为："+userName+" 具体错误信息为："+e);
            }
            return new DataPumpException("执行的SQL为: "+querySql+" 具体错误信息为："+e);
        }
        if (dataBaseType.equals(DataBaseType.Oracle)){
            DBUtilErrorCode dbUtilErrorCode = oracleQueryErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_QUERY_TABLE_NAME_ERROR && table != null){
                return new DataPumpException("表名为："+table+" 执行的SQL为:"+querySql+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_QUERY_SELECT_PRI_ERROR){
                return new DataPumpException("用户名为："+userName+" 具体错误信息为："+e);
            }
            return new DataPumpException("执行的SQL为: "+querySql+" 具体错误信息为："+e);
        }
        return new DataPumpException("执行的SQL为: "+querySql+" 具体错误信息为："+e);
    }

    public static DBUtilErrorCode mySqlQueryErrorAna(String e){
        if (e.contains(Constant.MYSQL_TABLE_NAME_ERR1) && e.contains(Constant.MYSQL_TABLE_NAME_ERR2)){
            return DBUtilErrorCode.MYSQL_QUERY_TABLE_NAME_ERROR;
        }else if (e.contains(Constant.MYSQL_SELECT_PRI)){
            return DBUtilErrorCode.MYSQL_QUERY_SELECT_PRI_ERROR;
        }else if (e.contains(Constant.MYSQL_COLUMN1) && e.contains(Constant.MYSQL_COLUMN2)){
            return DBUtilErrorCode.MYSQL_QUERY_COLUMN_ERROR;
        }else if (e.contains(Constant.MYSQL_WHERE)){
            return DBUtilErrorCode.MYSQL_QUERY_SQL_ERROR;
        }
        return DBUtilErrorCode.READ_RECORD_FAIL;
    }

    public static DBUtilErrorCode oracleQueryErrorAna(String e){
        if (e.contains(Constant.ORACLE_TABLE_NAME)){
            return DBUtilErrorCode.ORACLE_QUERY_TABLE_NAME_ERROR;
        }else if (e.contains(Constant.ORACLE_SQL)){
            return DBUtilErrorCode.ORACLE_QUERY_SQL_ERROR;
        }else if (e.contains(Constant.ORACLE_SELECT_PRI)){
            return DBUtilErrorCode.ORACLE_QUERY_SELECT_PRI_ERROR;
        }
        return DBUtilErrorCode.READ_RECORD_FAIL;
    }
}
