package com.orvillex.bortus.job.biz.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 心跳参数
 * @author y-z-f
 * @version 0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IdleBeatParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private Long jobId;
}
