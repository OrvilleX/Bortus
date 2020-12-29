package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日志请求参数
 * @author y-z-f
 * @version 0.1
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logDateTim;
    private long logId;
    private int fromLineNum;
}
