package com.orvillex.bortus.manager.modules.system.domain;

import com.orvillex.bortus.manager.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 字典领域
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Getter
@Setter
@Table(name = "sys_dict")
public class Dict extends BaseEntity {

    @Id
    @Column(name = "dict_id")
    @NotNull(groups = Update.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "dict", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<DictDetail> dictDetails;

    @NotBlank
    private String name;

    private String description;
}
