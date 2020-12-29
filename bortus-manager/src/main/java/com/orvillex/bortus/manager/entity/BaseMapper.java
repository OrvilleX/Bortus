package com.orvillex.bortus.manager.entity;

import java.util.List;

/**
 * 基础映射接口
 * 
 * @author y-z-f
 * @version 0.1
 */
public interface BaseMapper<D, E> {
    
    /**
     * DTO转Entity
     */
    E toEntity(D dto);

    /**
     * Entity转DTO
     */
    D toDto(E entity);

    /**
     * DTO集合转Entity集合
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Entity集合转DTO集合
     */
    List<D> toDto(List<E> entityList);
}
