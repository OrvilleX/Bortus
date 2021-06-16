package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询单据请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryTicketPagesReqVo implements Serializable {
    private String appId;

    private String startTime;

    private String endTime;

    private PostBackParameterVo postBackParameter;
}
