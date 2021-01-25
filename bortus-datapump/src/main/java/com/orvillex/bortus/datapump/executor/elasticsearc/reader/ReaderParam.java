package com.orvillex.bortus.datapump.executor.elasticsearc.reader;

import java.util.List;

import com.orvillex.bortus.datapump.executor.elasticsearc.AbstractParam;

import lombok.Data;

@Data
public class ReaderParam extends AbstractParam {
    private String index;
    private String type;
    private List<String> column;
    private Integer pageSize;
    private String condition;
    private String field;
    private String shards;
}
