package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务触发参数
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class TriggerParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private Long jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private Long logId;
    private Long logDateTime;

    private String glueType;
    private String glueSource;
    private Long glueUpdatetime;

    private Integer broadcastIndex;
    private Integer broadcastTotal;

    @Override
    public String toString() {
        return "TriggerParam{" +
                "jobId=" + jobId +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParams='" + executorParams + '\'' +
                ", executorBlockStrategy='" + executorBlockStrategy + '\'' +
                ", executorTimeout=" + executorTimeout +
                ", logId=" + logId +
                ", logDateTime=" + logDateTime +
                ", glueType='" + glueType + '\'' +
                ", glueSource='" + glueSource + '\'' +
                ", glueUpdatetime=" + glueUpdatetime +
                ", broadcastIndex=" + broadcastIndex +
                ", broadcastTotal=" + broadcastTotal +
                '}';
    }
}
