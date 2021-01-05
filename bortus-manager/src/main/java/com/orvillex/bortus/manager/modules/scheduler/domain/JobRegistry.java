package com.orvillex.bortus.manager.modules.scheduler.domain;

import javax.persistence.*;

import com.orvillex.bortus.manager.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 执行器，记录执行器注册信息
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_registry")
public class JobRegistry extends BaseEntity {
    private static final long serialVersionUID = 6991226757281285921L;

    @Id
    @Column(name = "registry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registryGroup;
    
    private String registryKey;

    private String registryValue;
}
