package com.orvillex.bortus.datapump.core.statistics;

import lombok.Data;

@Data
public class Metrics {
    private String writeSpeed;
    private String percentage;
    private String recordSpeed;
}
