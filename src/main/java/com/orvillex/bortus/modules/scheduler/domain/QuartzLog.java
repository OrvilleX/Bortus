package com.orvillex.bortus.modules.scheduler.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 调度任务日志模型
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Data
@Table(name = "sys_quartz_log")
public class QuartzLog implements Serializable {
    private static final long serialVersionUID = 4934329880461811209L;

    @Id
    @Column(name = "log_id")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "任务名称", hidden = true)
    private String jobName;

    @ApiModelProperty(value = "bean名称", hidden = true)
    private String beanName;

    @ApiModelProperty(value = "方法名称", hidden = true)
    private String methodName;

    @ApiModelProperty(value = "参数", hidden = true)
    private String params;

    @ApiModelProperty(value = "cron表达式", hidden = true)
    private String cronExpression;

    @ApiModelProperty(value = "状态", hidden = true)
    private Boolean isSuccess;

    @ApiModelProperty(value = "异常详情", hidden = true)
    private String exceptionDetail;

    @ApiModelProperty(value = "执行耗时", hidden = true)
    private Long time;

    @CreationTimestamp
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;
}
