package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询所有门店信息请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryAllUserReqVo implements Serializable {
    private String appId;
    /**
     * 分页参数
     */
    private PostBackParameterVo postBackParameter;
}
