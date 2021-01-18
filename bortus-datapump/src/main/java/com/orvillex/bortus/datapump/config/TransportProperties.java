package com.orvillex.bortus.datapump.config;

import lombok.Data;

@Data
public class TransportProperties {
    private int channelCapacity = 2048;
    private long channelSpeedByte = 1024 * 1024;
    private long channelSpeedRecord = 10000;
    private long channelFlowControlInterval = 1000;
    private int channelCapacityByte = 8 * 1024 * 1024;
    private int exchangerBufferSize = 32;
}
