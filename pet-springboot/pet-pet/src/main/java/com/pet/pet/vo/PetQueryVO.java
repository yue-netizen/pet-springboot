package com.pet.pet.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PetQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private String breed;
    private String age;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
