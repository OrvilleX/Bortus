package com.orvillex.bortus.manager.modules.scheduler.domain;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    private String beanName;

    private String methodName;

    private String params;

    private String cronExpression;

    private Boolean isSuccess;

    private String exceptionDetail;

    private Long time;

    @CreationTimestamp
    private Timestamp createTime;
}
