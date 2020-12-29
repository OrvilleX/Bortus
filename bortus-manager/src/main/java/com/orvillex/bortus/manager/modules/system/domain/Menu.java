package com.orvillex.bortus.manager.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orvillex.bortus.manager.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 菜单
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Getter
@Setter
@Table(name = "sys_menu")
public class Menu extends BaseEntity implements Serializable {

    @Id
    @Column(name = "menu_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(mappedBy = "menus")
    private Set<Role> roles;

    private String title;

    @Column(name = "name")
    private String componentName;

    private Integer menuSort = 999;

    private String component;

    private String path;

    private Integer type;

    private String permission;

    private String icon;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean cache;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean hidden;

    private Long pid;

    private Integer subCount = 0;

    private Boolean iFrame;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
