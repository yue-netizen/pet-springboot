package com.pet.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Long pages;
    private Long current;
    private Long size;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Long pages, Long current, Long size, List<T> records) {
        this.total = total;
        this.pages = pages;
        this.current = current;
        this.size = size;
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, Long pages, Long current, Long size, List<T> records) {
        return new PageResult<>(total, pages, current, size, records);
    }
}
