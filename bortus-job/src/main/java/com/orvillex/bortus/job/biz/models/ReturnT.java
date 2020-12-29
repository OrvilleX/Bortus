package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用返回对象
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;

	public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
	public static final ReturnT<String> FAIL = new ReturnT<String>(FAIL_CODE, null);

	private int code;
	private String msg;
	private T content;

    public ReturnT(){}
    
	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
    }
    
	public ReturnT(T content) {
		this.code = SUCCESS_CODE;
		this.content = content;
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
	}
}
