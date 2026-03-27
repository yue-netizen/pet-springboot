package com.pet.recruitment.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class JobQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String type;
    private String location;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
