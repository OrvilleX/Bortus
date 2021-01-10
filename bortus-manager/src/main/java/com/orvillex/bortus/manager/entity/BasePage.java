package com.orvillex.bortus.manager.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePage<T> {
    private long totalElements;
    private List<T> content;
}
