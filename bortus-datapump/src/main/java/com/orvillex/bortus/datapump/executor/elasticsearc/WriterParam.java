package com.orvillex.bortus.datapump.executor.elasticsearc;

import java.util.List;

import lombok.Data;

@Data
public class WriterParam extends AbstractParam {
    private String index;
    private String type;
    private String field;
    private List<String> column;
    private Integer concurrent = 1;
    private Integer bulkNum = 1000;
    private Boolean refresh = false;
}
