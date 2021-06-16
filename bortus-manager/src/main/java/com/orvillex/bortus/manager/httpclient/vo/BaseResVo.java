package com.orvillex.bortus.manager.httpclient.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 回应基础
 */
@Data
@AllArgsConstructor
public class BaseResVo<T> {
    
    /**
     * 是否正确处理请求，返回success或error，不区分大小写
     */
    private String status;

    /**
     * 处理结果消息
     */
    private List<String> messages;

    /**
     * 数据体
     */
    private T data;
}
