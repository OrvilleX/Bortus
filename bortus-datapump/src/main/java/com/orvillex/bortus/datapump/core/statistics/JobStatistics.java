package com.orvillex.bortus.datapump.core.statistics;

import java.util.Date;

import lombok.Data;

@Data
public class JobStatistics {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private Long instId;
    private String appName;
    private Long jobVersion;
    private Integer taskGroupId;
    private Date windowStart;
    private Date windowEnd;
    private Date jobStartTime;
    private Date jobEndTime;
    private Long jobRunTimeMs;
    private Integer jobPriority;
    private Integer channelNum;
    private String cluster;
    private String jobDomain;
    private String srcType;
    private String dstType;
    private String srcGuid;
    private String dstGuid;
    private Long records;
    private Long bytes;
    private Long speedRecord;
    private Long speedByte;
    private String stagePercent;
    private Long errorRecord;
    private Long errorBytes;
    private Long waitReadTimeMs;
    private Long waitWriteTimeMs;
    private Long odpsBlockCloseTimeMs;
    private Long sqlQueryTimeMs;
    private Long resultNextTimeMs;
    private Long taskTotalTimeMs;
    private String hostAddress;
}
