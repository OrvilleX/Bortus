package com.orvillex.bortus.datapump.executor.mongo.writer;

import java.util.List;

import com.orvillex.bortus.datapump.executor.mongo.AbstractParam;

import lombok.Data;

@Data
public class WriteParam extends AbstractParam {
    private String username;
    private String password;
    private String database;
    private String collectionName;
    private List<ColumnEntry> column;
    private WriteMode writeMode;

    @Data
    public static class ColumnEntry {
        private String name;
        private String type;
        private String splitter;
        private String itemType;
    }

    @Data
    public static class WriteMode {
        private String replaceKey;
        private Boolean isReplace = false;
    }
}
