package com.orvillex.bortus.manager.modules.system.domain;

import com.orvillex.bortus.manager.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典明细
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Getter
@Setter
@Table(name = "sys_dict_detail")
public class DictDetail extends BaseEntity implements Serializable {

    @Id
    @Column(name = "detail_id")
    @NotNull(groups = Update.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "dict_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Dict dict;

    private String labal;

    private String value;

    private Integer dictSort = 999;
}
