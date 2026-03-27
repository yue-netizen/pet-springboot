package com.pet.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String breed;
    private String age;
    private String type;
    private String gender;
    private String image;
    private String description;
    private String healthStatus;
    private Integer status;
    private Long shelterId;
}
