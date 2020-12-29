package com.orvillex.modules.system.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orvillex.entity.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 部门DTO
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
public class DeptDto extends BaseDTO implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
    private Integer deptSort;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DeptDto> children;
    private Long pid;
    private Integer subCount;

    public Boolean getHasChildren() {
        return subCount > 0;
    }

    public Boolean getLeaf() {
        return subCount <= 0;
    }

    public String getLabel() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeptDto deptDto = (DeptDto) o;
        return Objects.equals(id, deptDto.id) &&
                Objects.equals(name, deptDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
