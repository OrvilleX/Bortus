package com.orvillex.bortus.manager.modules.scheduler.domain;

import java.util.Date;

import javax.persistence.*;

import com.orvillex.bortus.manager.entity.BaseEntity;

import lombok.*;

/**
 * 执行任务信息
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_info")
public class JobInfo extends BaseEntity {
    private static final long serialVersionUID = -3308375525671972395L;

    @Id
    @Column(name = "info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 执行器主键ID
     */
    private Long jobGroup;

    /**
     * 任务执行CRON表达式
     */
	private String jobCron;
	private String jobDesc;
    
    /**
     * 负责人
     */
    private String author;
    
    /**
     * 报警邮件
     */
    private String alarmEmail;
    
    /**
     * 执行器路由策略
     */
    private String executorRouteStrategy;
    
    /**
     * 执行器，任务Handler名称
     */
    private String executorHandler;
    
    /**
     * 执行器，任务参数
     */
    private String executorParam;
    
    /**
     * 阻塞处理策略
     */
    private String executorBlockStrategy;
    
    /**
     * 任务执行超时时间，单位秒
     */
    private int executorTimeout;
    
    /**
     * 失败重试次数
     */
	private int executorFailRetryCount;
    
    /**
     * GLUE类型
     */
    private String glueType;
    
    /**
     * GLUE源代码
     */
    private String glueSource;
    
    /**
     * GLUE备注
     */
    private String glueRemark;
    
    /**
     * GLUE更新时间
     */
    private Date glueUpdatetime;
    
    /**
     * 子任务ID，多个逗号分隔
     */
    private String childJobId;
    
    /**
     * 调度状态：0-停止，1-运行
     */
    private int triggerStatus;
    
    /**
     * 上次调度时间
     */
    private long triggerLastTime;
    
    /**
     * 下次调度时间
     */
	private long triggerNextTime;
}
