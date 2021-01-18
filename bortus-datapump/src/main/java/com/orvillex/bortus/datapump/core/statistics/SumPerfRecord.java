package com.orvillex.bortus.datapump.core.statistics;

public class SumPerfRecord {
    private long perfTimeTotal = 0;
    private long averageTime = 0;
    private long maxTime = 0;
    private String appName = "";
    private int totalCount = 0;
    private long recordsTotal = 0;
    private long sizesTotal = 0;
    private long averageRecords = 0;
    private long averageBytes = 0;
    private long maxRecord = 0;
    private long maxByte = 0;

    public void add(PerfRecord perfRecord) {
        if (perfRecord == null) {
            return;
        }
        perfTimeTotal += perfRecord.getElapsedTimeInNs();
        if (perfRecord.getElapsedTimeInNs() >= maxTime) {
            maxTime = perfRecord.getElapsedTimeInNs();
            appName = perfRecord.getAppName();
        }

        recordsTotal += perfRecord.getCount();
        sizesTotal += perfRecord.getSize();
        if (perfRecord.getCount() >= maxRecord) {
            maxRecord = perfRecord.getCount();
            maxByte = perfRecord.getSize();
        }

        totalCount++;
    }

    public long getPerfTimeTotal() {
        return perfTimeTotal;
    }

    public long getAverageTime() {
        if (totalCount > 0) {
            averageTime = perfTimeTotal / totalCount;
        }
        return averageTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public long getSizesTotal() {
        return sizesTotal;
    }

    public long getAverageRecords() {
        if (totalCount > 0) {
            averageRecords = recordsTotal / totalCount;
        }
        return averageRecords;
    }

    public long getAverageBytes() {
        if (totalCount > 0) {
            averageBytes = sizesTotal / totalCount;
        }
        return averageBytes;
    }

    public long getMaxRecord() {
        return maxRecord;
    }

    public long getMaxByte() {
        return maxByte;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
