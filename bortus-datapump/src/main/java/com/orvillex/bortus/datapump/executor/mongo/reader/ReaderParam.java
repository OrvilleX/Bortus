package com.orvillex.bortus.datapump.executor.mongo.reader;

import java.util.List;

import com.orvillex.bortus.datapump.executor.mongo.AbstractParam;

import lombok.Data;

@Data
public class ReaderParam extends AbstractParam {
    private String username;
    private String password;
    private String database;
    private String authdb;
    private String collectionName;
    private String query;
    private List<ColumnEntry> column;

    @Data
    public static class ColumnEntry {
        private String name;
        private String type;
        private String splitter;
    }
}
