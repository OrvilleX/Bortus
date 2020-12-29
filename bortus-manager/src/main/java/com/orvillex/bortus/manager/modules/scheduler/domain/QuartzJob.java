package com.orvillex.bortus.manager.modules.scheduler.domain;

import com.orvillex.bortus.manager.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 调度任务模型
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_quartz_job")
public class QuartzJob extends BaseEntity {
    private static final long serialVersionUID = 3199386249250249753L;

    public static final String JOB_KEY = "JOB_KEY";

    @Id
    @Column(name = "job_id")
    @NotNull(groups = {BaseEntity.Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String uuid;

    private String jobName;

    @NotBlank
    private String beanName;

    @NotBlank
    private String methodName;

    private String params;

    @NotBlank
    private String cronExpression;

    private Boolean isPause = false;

    private String personInCharge;

    private String email;

    private String subTask;

    private Boolean pauseAfterFailure;

    @NotBlank
    private String description;
}
