package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 回调参数
 * @author y-z-f
 * @version 0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;
    private long logDateTim;
    private ReturnT<String> executeResult;

    @Override
    public String toString() {
        return "HandleCallbackParam{" +
                "logId=" + logId +
                ", logDateTim=" + logDateTim +
                ", executeResult=" + executeResult +
                '}';
    }
}
