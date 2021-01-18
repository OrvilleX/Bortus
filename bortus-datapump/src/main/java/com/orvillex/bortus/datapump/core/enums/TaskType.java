package com.orvillex.bortus.datapump.core.enums;

/**
 * 任务类型
 */
public enum TaskType {
    READER("reader"), WRITER("writer");

    private String pluginType;

    private TaskType(String pluginType) {
        this.pluginType = pluginType;
    }

    @Override
    public String toString() {
        return this.pluginType;
    }
}
