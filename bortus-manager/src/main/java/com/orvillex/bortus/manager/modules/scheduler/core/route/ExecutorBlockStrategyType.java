package com.orvillex.bortus.manager.modules.scheduler.core.route;

public enum ExecutorBlockStrategyType {

    SERIAL_EXECUTION("Serial execution"), DISCARD_LATER("Discard Later"), COVER_EARLY("Cover Early");

    private String title;

    private ExecutorBlockStrategyType(String title) {
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
            for (ExecutorBlockStrategyType item : ExecutorBlockStrategyType.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
