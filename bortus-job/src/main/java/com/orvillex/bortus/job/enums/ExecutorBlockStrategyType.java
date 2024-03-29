package com.orvillex.bortus.job.enums;

/**
 * 任务执行方式
 */
public enum ExecutorBlockStrategyType {
    /**
     * 单机串行
     */
    SERIAL_EXECUTION("Serial execution"),
    /**
     * 丢弃后续调度
     */
    DISCARD_LATER("Discard Later"),
    /**
     * 覆盖之前调度
     */
    COVER_EARLY("Cover Early");

    private String title;
    private ExecutorBlockStrategyType (String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public static ExecutorBlockStrategyType match(String name, ExecutorBlockStrategyType defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyType item:ExecutorBlockStrategyType.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
