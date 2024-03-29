package com.orvillex.bortus.manager.utils;


/**
 * SpringContextHolder 初始化调用的回调接口
 * @author y-z-f
 * @version 0.1
 */
public interface CallBack {
    /**
     * 回调方法
     */
    void executor();

    /**
     * 回调任务名
     */
    default String getCallBackName() {
        return Thread.currentThread().getId() + ":" + this.getClass().getName();
    }
}
