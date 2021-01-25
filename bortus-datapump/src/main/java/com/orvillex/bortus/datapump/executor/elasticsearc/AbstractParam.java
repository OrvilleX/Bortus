package com.orvillex.bortus.datapump.executor.elasticsearc;

import java.util.List;

import lombok.Data;

@Data
public abstract class AbstractParam {
    private List<String> connections;
}
