package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页用参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostBackParameterVo implements Serializable {
    private String parameterType;
    private String parameterValue;
}
