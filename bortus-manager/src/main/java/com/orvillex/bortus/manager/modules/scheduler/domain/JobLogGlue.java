package com.orvillex.bortus.manager.modules.scheduler.domain;

import javax.persistence.*;

import com.orvillex.bortus.manager.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务Glue日志
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_logglue")
public class JobLogGlue extends BaseEntity {
    private static final long serialVersionUID = -3182239531822607125L;

    @Id
    @Column(name = "logglue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务主键ID
     */
    private Long jobId;
    
    /**
     * GLUE类型
     */
	private String glueType;
	private String glueSource;
	private String glueRemark;
}
