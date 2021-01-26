package com.orvillex.bortus.datapump.executor.core.writer;

import java.util.List;

import com.orvillex.bortus.datapump.executor.core.CommonParam;

import lombok.Data;

@Data
public class CommonWriterParam extends CommonParam {
    private String username;
    private String password;
    private String jbdc;
    private String mandatoryEncoding;
    private String querySql;
    private String table;
    private List<String> column;
    private Integer batchSize = 2048;
    private Integer batchByteSize = 32 * 1024 * 1024;
    private String writeMode = "INSERT";
    private Boolean emptyAsNull = true;
    private String insertOrReplaceTemplate;
    private Integer tableNumber;
}
