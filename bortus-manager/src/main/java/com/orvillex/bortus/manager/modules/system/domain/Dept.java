package com.orvillex.bortus.manager.modules.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.orvillex.bortus.manager.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 部门领域类
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Getter
@Setter
@Table(name = "sys_dept")
public class Dept extends BaseEntity implements Serializable {

    @Id
    @Column(name = "dept_id")
    @NotNull(groups = Update.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "depts")
    private Set<Role> roles;

    private Integer deptSort;

    @NotBlank
    private String name;

    @NotNull
    private Boolean enabled;

    private Long pid;

    private Integer subCount = 0;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Dept dept = (Dept)obj;
        return Objects.equals(id, dept.id) &&
                Objects.equals(name, dept.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
