package com.orvillex.bortus.manager.modules.scheduler.domain;

import java.util.Date;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务日志，用于跟踪记录触发过程
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_log")
public class JobLog {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	// ---- 任务信息
	private Long jobGroup;
	private Long jobId;

	// ---- 执行信息
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
	private String executorShardingParam;
	private Integer executorFailRetryCount;
	
	// ---- 触发信息
	private Date triggerTime;
	private Integer triggerCode;
	private String triggerMsg;
	
	// ---- 处理器信息
	private Date handleTime;
	private Integer handleCode;
	private String handleMsg;

	// ---- 告警信息
	private Integer alarmStatus;
}
