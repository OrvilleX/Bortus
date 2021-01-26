package com.orvillex.bortus.datapump.executor.core.reader;

import com.orvillex.bortus.datapump.executor.core.CommonParam;

import lombok.Data;

@Data
public class CommonReaderParam extends CommonParam {
    private String username;
    private String password;
    private String jbdc;
    private String mandatoryEncoding;
    private String querySql;
    private String table;
}
