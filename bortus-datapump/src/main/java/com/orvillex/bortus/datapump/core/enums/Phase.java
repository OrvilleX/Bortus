package com.orvillex.bortus.datapump.core.enums;

public enum Phase {
    TASK_TOTAL(0), READ_TASK_INIT(1), READ_TASK_PREPARE(2), READ_TASK_DATA(3), READ_TASK_POST(4), READ_TASK_DESTROY(5),
    WRITE_TASK_INIT(6), WRITE_TASK_PREPARE(7), WRITE_TASK_DATA(8), WRITE_TASK_POST(9), WRITE_TASK_DESTROY(10),
    SQL_QUERY(100), RESULT_NEXT_ALL(101), ODPS_BLOCK_CLOSE(102), WAIT_READ_TIME(103), WAIT_WRITE_TIME(104),
    TRANSFORMER_TIME(201);

    private int val;

    Phase(int val) {
        this.val = val;
    }

    public int toInt() {
        return val;
    }
}
