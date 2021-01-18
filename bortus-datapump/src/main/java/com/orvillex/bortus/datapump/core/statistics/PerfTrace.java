package com.orvillex.bortus.datapump.core.statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.datapump.core.runner.JobInfo;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.StringUtils;

public class PerfTrace {
    private static PerfTrace instance;
    private static final Object lock = new Object();
    private String perfTraceId;
    private volatile boolean enable;
    private long instId;
    private long jobVersion;
    private String appName;

    private int priority;
    private int batchSize = 500;
    private volatile boolean perfReportEnable = true;

    private Map<String, String> taskDetails = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<Phase, SumPerfRecord> perfRecordMapsprint = new ConcurrentHashMap<Phase, SumPerfRecord>();
    private SumPerfReport sumPerf4Report = new SumPerfReport();
    private SumPerfReport sumPerf4Report4NotEnd;
    private final Set<PerfRecord> needReportPool4NotEnd = new HashSet<PerfRecord>();
    private final List<PerfRecord> totalEndReport = new ArrayList<PerfRecord>();

    public static PerfTrace getInstance(String appName, int priority, boolean enable) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new PerfTrace(appName, priority, enable);
                }
            }
        }
        return instance;
    }

    public static PerfTrace getInstance() {
        if (instance == null) {
            JobLogger.log("PerfTrace instance not be init! must have some error! ");
            synchronized (lock) {
                if (instance == null) {
                    instance = new PerfTrace("default", 0, false);
                }
            }
        }
        return instance;
    }

    private PerfTrace(String appName, int priority, boolean enable) {
        try {
            this.perfTraceId = "job_" + appName;
            this.enable = enable;
            this.priority = priority;
            JobLogger.log(String.format("PerfTrace traceId=%s, isEnable=%s, priority=%s", this.perfTraceId, this.enable,
                    this.priority));
        } catch (Exception e) {
            this.enable = false;
        }
    }

    public void addTaskDetails(String appName, String detail) {
        if (enable) {
            String before = "";
            int index = detail.indexOf("?");
            String current = detail.substring(0, index == -1 ? detail.length() : index);
            if (current.indexOf("[") >= 0) {
                current += "]";
            }
            if (taskDetails.containsKey(appName)) {
                before = taskDetails.get(appName).trim();
            }
            if (StringUtils.isEmpty(before)) {
                before = "";
            } else {
                before += ",";
            }
            this.taskDetails.put(appName, before + current);
        }
    }

    public void tracePerfRecord(PerfRecord perfRecord) {
        try {
            if (enable) {
                long curNanoTime = System.nanoTime();
                switch (perfRecord.getAction()) {
                    case end:
                        synchronized (totalEndReport) {
                            totalEndReport.add(perfRecord);
                            if (totalEndReport.size() > batchSize * 10) {
                                sumPerf4EndPrint(totalEndReport);
                            }
                        }
                        if (perfReportEnable && needReport(perfRecord)) {
                            synchronized (needReportPool4NotEnd) {
                                sumPerf4Report.add(curNanoTime, perfRecord);
                                needReportPool4NotEnd.remove(perfRecord);
                            }
                        }
                        break;
                    case start:
                        if (perfReportEnable && needReport(perfRecord)) {
                            synchronized (needReportPool4NotEnd) {
                                needReportPool4NotEnd.add(perfRecord);
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean needReport(PerfRecord perfRecord) {
        switch (perfRecord.getPhase()) {
            case TASK_TOTAL:
            case SQL_QUERY:
            case RESULT_NEXT_ALL:
            case ODPS_BLOCK_CLOSE:
                return true;
        }
        return false;
    }

    public String summarizeNoException() {
        String res;
        try {
            res = summarize();
        } catch (Exception e) {
            res = "PerfTrace summarize has Exception " + e.getMessage();
        }
        return res;
    }

    private synchronized String summarize() {
        if (!enable) {
            return "PerfTrace not enable!";
        }

        if (totalEndReport.size() > 0) {
            sumPerf4EndPrint(totalEndReport);
        }

        StringBuilder info = new StringBuilder();
        info.append("\n === total summarize info === \n");
        info.append("\n   1. all phase average time info and max time task info: \n\n");
        info.append(String.format("%-20s | %18s | %18s | %18s | %18s | %-100s\n", "PHASE", "AVERAGE USED TIME",
                "ALL TASK NUM", "MAX USED TIME", "MAX TASK ID", "MAX TASK INFO"));

        List<Phase> keys = new ArrayList<Phase>(perfRecordMapsprint.keySet());
        Collections.sort(keys, new Comparator<Phase>() {
            @Override
            public int compare(Phase o1, Phase o2) {
                return o1.toInt() - o2.toInt();
            }
        });
        for (Phase phase : keys) {
            SumPerfRecord sumPerfRecord = perfRecordMapsprint.get(phase);
            if (sumPerfRecord == null) {
                continue;
            }
            long averageTime = sumPerfRecord.getAverageTime();
            long maxTime = sumPerfRecord.getMaxTime();
            info.append(String.format("%-20s | %18s | %18s | %18s | %18s | %-100s\n", phase, unitTime(averageTime),
                    sumPerfRecord.getTotalCount(), unitTime(maxTime), appName, taskDetails.get(appName)));
        }
        SumPerfRecord countSumPerf = perfRecordMapsprint.get(Phase.READ_TASK_DATA);
        if (countSumPerf == null) {
            countSumPerf = new SumPerfRecord();
        }

        long averageRecords = countSumPerf.getAverageRecords();
        long averageBytes = countSumPerf.getAverageBytes();
        long maxRecord = countSumPerf.getMaxRecord();
        long maxByte = countSumPerf.getMaxByte();

        info.append("\n\n 2. record average count and max count task info :\n\n");
        info.append(String.format("%-20s | %18s | %18s | %18s | %18s | %18s | %-100s\n", "PHASE", "AVERAGE RECORDS",
                "AVERAGE BYTES", "MAX RECORDS", "MAX RECORD`S BYTES", "MAX TASK ID", "MAX TASK INFO"));
        info.append(String.format("%-20s | %18s | %18s | %18s | %18s | %18s | %-100s\n", Phase.READ_TASK_DATA,
                averageRecords, unitSize(averageBytes), maxRecord, unitSize(maxByte), appName,
                taskDetails.get(appName)));
        return info.toString();
    }

    public static String unitTime(long time) {
        return unitTime(time, TimeUnit.NANOSECONDS);
    }

    public static String unitTime(long time, TimeUnit timeUnit) {
        return String.format("%,.3fs", ((float) timeUnit.toNanos(time)) / 1000000000);
    }

    public static String unitSize(long size) {
        if (size > 1000000000) {
            return String.format("%,.2fG", (float) size / 1000000000);
        } else if (size > 1000000) {
            return String.format("%,.2fM", (float) size / 1000000);
        } else if (size > 1000) {
            return String.format("%,.2fK", (float) size / 1000);
        } else {
            return size + "B";
        }
    }

    public synchronized ConcurrentHashMap<Phase, SumPerfRecord> getPerfRecordMaps4print() {
        if (totalEndReport.size() > 0) {
            sumPerf4EndPrint(totalEndReport);
        }
        return perfRecordMapsprint;
    }

    public SumPerfReport getSumPerf4Report() {
        return sumPerf4Report;
    }

    public Set<PerfRecord> getNeedReportPool4NotEnd() {
        return needReportPool4NotEnd;
    }

    public List<PerfRecord> getTotalEndReport() {
        return totalEndReport;
    }

    public Map<String, String> getTaskDetails() {
        return taskDetails;
    }

    public boolean isEnable() {
        return enable;
    }

    private String cluster;
    private String jobDomain;
    private String srcType;
    private String dstType;
    private String srcGuid;
    private String dstGuid;
    private Date windowStart;
    private Date windowEnd;
    private Date jobStartTime;

    public void setJobInfo(JobInfo jobInfo, boolean perfReportEnable) {
        try {
            if (jobInfo != null && perfReportEnable) {

                cluster = jobInfo.getCluster();

                String srcDomain = jobInfo.getSrcDomain();
                String dstDomain = jobInfo.getDstDomain();
                jobDomain = srcDomain + "|" + dstDomain;
                srcType = jobInfo.getScrType();
                dstType = jobInfo.getDstType();
                srcGuid = jobInfo.getSrcGuid();
                dstGuid = jobInfo.getDstGuid();
                windowStart = getWindow(jobInfo.getWindowStart(), true);
                windowEnd = getWindow(jobInfo.getWindowEnd(), false);
                String jobVersionStr = jobInfo.getVersion();
                jobVersion = StringUtils.isEmpty(jobVersionStr) ? (long) -4 : Long.parseLong(jobVersionStr);
                jobStartTime = new Date();
            }
            this.perfReportEnable = perfReportEnable;
        } catch (Exception e) {
            this.perfReportEnable = false;
        }
    }

    private Date getWindow(String windowStr, boolean startWindow) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (StringUtils.isNotEmpty(windowStr)) {
            try {
                return sdf1.parse(windowStr);
            } catch (ParseException e) {
                // do nothing
            }
        }

        if (startWindow) {
            try {
                return sdf2.parse(sdf2.format(new Date()));
            } catch (ParseException e1) {
                // do nothing
            }
        }

        return null;
    }

    public long getInstId() {
        return instId;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public synchronized JobStatistics getReports() {

        try {
            if (!enable || !perfReportEnable) {
                return null;
            }

            sumPerf4Report4NotEnd = new SumPerfReport();
            Set<PerfRecord> needReportPool4NotEndTmp = null;
            synchronized (needReportPool4NotEnd) {
                needReportPool4NotEndTmp = new HashSet<PerfRecord>(needReportPool4NotEnd);
            }

            long curNanoTime = System.nanoTime();
            for (PerfRecord perfRecord : needReportPool4NotEndTmp) {
                sumPerf4Report4NotEnd.add(curNanoTime, perfRecord);
            }

            JobStatistics jdo = new JobStatistics();
            jdo.setInstId(this.instId);
            jdo.setAppName(this.appName);
            jdo.setJobVersion(this.jobVersion);
            jdo.setWindowStart(this.windowStart);
            jdo.setWindowEnd(this.windowEnd);
            jdo.setJobStartTime(jobStartTime);
            jdo.setJobRunTimeMs(System.currentTimeMillis() - jobStartTime.getTime());
            jdo.setJobPriority(this.priority);
            jdo.setCluster(this.cluster);
            jdo.setJobDomain(this.jobDomain);
            jdo.setSrcType(this.srcType);
            jdo.setDstType(this.dstType);
            jdo.setSrcGuid(this.srcGuid);
            jdo.setDstGuid(this.dstGuid);
            jdo.setTaskTotalTimeMs(sumPerf4Report4NotEnd.totalTaskRunTimeInMs + sumPerf4Report.totalTaskRunTimeInMs);
            jdo.setOdpsBlockCloseTimeMs(sumPerf4Report4NotEnd.odpsCloseTimeInMs + sumPerf4Report.odpsCloseTimeInMs);
            jdo.setSqlQueryTimeMs(sumPerf4Report4NotEnd.sqlQueryTimeInMs + sumPerf4Report.sqlQueryTimeInMs);
            jdo.setResultNextTimeMs(sumPerf4Report4NotEnd.resultNextTimeInMs + sumPerf4Report.resultNextTimeInMs);
            return jdo;
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }

    private void sumPerf4EndPrint(List<PerfRecord> totalEndReport) {
        if (!enable || totalEndReport == null) {
            return;
        }
        for (PerfRecord perfRecord : totalEndReport) {
            perfRecordMapsprint.putIfAbsent(perfRecord.getPhase(), new SumPerfRecord());
            perfRecordMapsprint.get(perfRecord.getPhase()).add(perfRecord);
        }
        totalEndReport.clear();
    }
}
