package com.orvillex.bortus.job.enums;

/**
 * 注册配置
 */
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    public enum RegistType{ EXECUTOR, ADMIN }

}
