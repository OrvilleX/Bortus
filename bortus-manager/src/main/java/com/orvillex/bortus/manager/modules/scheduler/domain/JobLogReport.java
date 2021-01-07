package com.orvillex.bortus.manager.modules.scheduler.domain;

import java.util.Date;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * 调度日志报表
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_logreport")
public class JobLogReport {
    @Id
    @Column(name = "logreport_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date triggerDay;

    private Long runningCount;
    private Long sucCount;
    private Long failCount;
}
