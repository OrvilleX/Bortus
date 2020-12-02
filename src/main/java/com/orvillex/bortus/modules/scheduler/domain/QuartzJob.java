package com.orvillex.bortus.modules.scheduler.domain;

import com.orvillex.bortus.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "用于子任务唯一标识", hidden = true)
    private String uuid;

    @ApiModelProperty(value = "定时器名称")
    private String jobName;

    @NotBlank
    @ApiModelProperty(value = "Bean名称")
    private String beanName;

    @NotBlank
    @ApiModelProperty(value = "方法名称")
    private String methodName;

    @ApiModelProperty(value = "参数")
    private String params;

    @NotBlank
    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;

    @ApiModelProperty(value = "状态，暂时或启动")
    private Boolean isPause = false;

    @ApiModelProperty(value = "负责人")
    private String personInCharge;

    @ApiModelProperty(value = "报警邮箱")
    private String email;

    @ApiModelProperty(value = "子任务")
    private String subTask;

    @ApiModelProperty(value = "失败后暂停")
    private Boolean pauseAfterFailure;

    @NotBlank
    @ApiModelProperty(value = "备注")
    private String description;
}
