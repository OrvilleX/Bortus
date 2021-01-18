package com.orvillex.bortus.datapump.core.statistics;

import java.util.Date;

import com.orvillex.bortus.datapump.core.enums.Action;
import com.orvillex.bortus.datapump.core.enums.Phase;
import com.orvillex.bortus.job.log.JobLogger;

import org.apache.commons.lang3.time.DateFormatUtils;

import lombok.Data;

/**
 * 性能记录
 */
@Data
public class PerfRecord implements Comparable<PerfRecord> {
    private static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    private final String appName;
    private final Phase phase;
    private volatile Action action;
    private volatile Date startTime;
    private volatile long elapsedTimeInNs = -1;
    private volatile long count = 0;
    private volatile long size = 0;
    private volatile long startTimeInNs;
    private volatile boolean isReport = false;

    public PerfRecord(String appName, Phase phase) {
        this.appName = appName;
        this.phase = phase;
        PerfTrace.getInstance(appName, 1, true);
    }

    public static void addPerfRecord(String appName, Phase phase, long startTime, long elapsedTimeInNs) {
        if (PerfTrace.getInstance().isEnable()) {
            PerfRecord perfRecord = new PerfRecord(appName, phase);
            perfRecord.elapsedTimeInNs = elapsedTimeInNs;
            perfRecord.action = Action.end;
            perfRecord.startTime = new Date(startTime);
            PerfTrace.getInstance().tracePerfRecord(perfRecord);
            JobLogger.log(perfRecord.toString());
        }
    }

    public void start() {
        if (PerfTrace.getInstance().isEnable()) {
            this.startTime = new Date();
            this.startTimeInNs = System.nanoTime();
            this.action = Action.start;
            PerfTrace.getInstance().tracePerfRecord(this);
            JobLogger.log(toString());
        }
    }

    public void addCount(long count) {
        this.count += count;
    }

    public void addSize(long size) {
        this.size += size;
    }

    public void end() {
        if (PerfTrace.getInstance().isEnable()) {
            this.elapsedTimeInNs = System.nanoTime() - startTimeInNs;
            this.action = Action.end;
            PerfTrace.getInstance().tracePerfRecord(this);
            JobLogger.log(toString());
        }
    }

    public void end(long elapsedTimeInNs) {
        if (PerfTrace.getInstance().isEnable()) {
            this.elapsedTimeInNs = elapsedTimeInNs;
            this.action = Action.end;
            PerfTrace.getInstance().tracePerfRecord(this);
            JobLogger.log(toString());
        }
    }

    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", getInstId(), appName, phase, action,
                DateFormatUtils.format(startTime, datetimeFormat), elapsedTimeInNs, count, size);
    }

    @Override
    public int compareTo(PerfRecord o) {
        if (o == null) {
            return 1;
        }
        return this.elapsedTimeInNs > o.elapsedTimeInNs ? 1 : this.elapsedTimeInNs == o.elapsedTimeInNs ? 0 : -1;
    }

    @Override
    public int hashCode() {
        long jobId = getInstId();
        int result = (int) (jobId ^ (jobId >>> 32));
        result = 31 * result + phase.toInt();
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PerfRecord)) {
            return false;
        }

        PerfRecord dst = (PerfRecord) o;

        if (this.getInstId() != dst.getInstId())
            return false;
        if (this.appName != dst.appName)
            return false;
        if (phase != null ? !phase.equals(dst.phase) : dst.phase != null)
            return false;
        if (startTime != null ? !startTime.equals(dst.startTime) : dst.startTime != null)
            return false;
        return true;
    }

    public PerfRecord copy() {
        PerfRecord copy = new PerfRecord(this.appName, this.phase);
        copy.action = this.action;
        copy.startTime = this.startTime;
        copy.elapsedTimeInNs = this.elapsedTimeInNs;
        copy.count = this.count;
        copy.size = this.size;
        return copy;
    }

    public long getInstId() {
        return PerfTrace.getInstance().getInstId();
    }

    public long getStartTimeInMs() {
        return startTime.getTime();
    }

    public String getDatetime() {
        if (startTime == null) {
            return "null time";
        }
        return DateFormatUtils.format(startTime, datetimeFormat);
    }
}
