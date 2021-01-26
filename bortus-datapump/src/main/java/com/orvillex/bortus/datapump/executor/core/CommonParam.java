package com.orvillex.bortus.datapump.executor.core;

import java.util.List;

import lombok.Data;

@Data
public abstract class CommonParam {
    private List<String> session;
}
