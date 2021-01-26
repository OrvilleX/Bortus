package com.orvillex.bortus.datapump.executor.mysql.reader;

import com.orvillex.bortus.datapump.executor.core.reader.CommonReaderParam;

import lombok.Data;

@Data
public class ReaderParam extends CommonReaderParam {
    private Integer fetchSize;
}
