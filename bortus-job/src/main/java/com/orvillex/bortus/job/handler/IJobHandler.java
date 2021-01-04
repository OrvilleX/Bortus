package com.orvillex.bortus.job.handler;

import java.lang.reflect.InvocationTargetException;

import com.orvillex.bortus.job.biz.models.ReturnT;

/**
 * 任务处理器
 */
public abstract class IJobHandler {
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);

	/**
	 * 处理器执行
	 */
	public abstract ReturnT<String> execute(String param) throws Exception;


	/**
	 * 初始化
	 */
	public void init() throws InvocationTargetException, IllegalAccessException {
		// do something
	}


	/**
	 * 销毁
	 */
	public void destroy() throws InvocationTargetException, IllegalAccessException {
		// do something
	}
}
