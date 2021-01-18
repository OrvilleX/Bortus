package com.orvillex.bortus.datapump.core.runner;

import lombok.Data;

@Data
public class JobInfo {
    private String cluster;
    private String srcDomain;
    private String dstDomain;
    private String scrType;
    private String dstType;
    private String srcGuid;
    private String dstGuid;
    private String windowStart;
    private String windowEnd;
    private String version;
}
