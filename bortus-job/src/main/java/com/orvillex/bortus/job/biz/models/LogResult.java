package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日志返回数据
 * @author y-z-f
 * @version 0.1
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogResult implements Serializable {
    private static final long serialVersionUID = 42L;

    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;
}
