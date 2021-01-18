package com.orvillex.bortus.datapump.core.statistics;

import lombok.Getter;

@Getter
public class SumPerfReport {
    long totalTaskRunTimeInMs = 0L;
    long odpsCloseTimeInMs = 0L;
    long sqlQueryTimeInMs = 0L;
    long resultNextTimeInMs = 0L;

    public void add(long curNanoTime, PerfRecord perfRecord) {
        try {
            long runTimeEndInMs;
            if (perfRecord.getElapsedTimeInNs() == -1) {
                runTimeEndInMs = (curNanoTime - perfRecord.getStartTimeInNs()) / 1000000;
            } else {
                runTimeEndInMs = perfRecord.getElapsedTimeInNs() / 1000000;
            }
            switch (perfRecord.getPhase()) {
                case TASK_TOTAL:
                    totalTaskRunTimeInMs += runTimeEndInMs;
                    break;
                case SQL_QUERY:
                    sqlQueryTimeInMs += runTimeEndInMs;
                    break;
                case RESULT_NEXT_ALL:
                    resultNextTimeInMs += runTimeEndInMs;
                    break;
                case ODPS_BLOCK_CLOSE:
                    odpsCloseTimeInMs += runTimeEndInMs;
                    break;
            }
        } catch (Exception e) {
            // do nothing
        }
    }
}
