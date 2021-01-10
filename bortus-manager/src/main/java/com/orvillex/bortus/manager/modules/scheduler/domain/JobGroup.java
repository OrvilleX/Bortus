package com.orvillex.bortus.manager.modules.scheduler.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.orvillex.bortus.manager.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 执行器组，用于实际执行调度任务
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_job_group")
public class JobGroup extends BaseEntity {
    private static final long serialVersionUID = -9002921753768935781L;

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String appName;
    
    @NotBlank
    private String title;

    /**
     * 执行器地址类型，0为自动注册，1为手动录入
     */
    private int addressType;

    /**
     * 执行器地址列表，多个地址逗号分隔
     */
    private String addressList;

    @Transient
    private List<String> registryList;
    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }
}
