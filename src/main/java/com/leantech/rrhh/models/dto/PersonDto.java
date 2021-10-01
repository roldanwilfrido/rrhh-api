package com.leantech.rrhh.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDto {
    private Integer id;

    private String name;

    private String lastName;

    private String address;

    private String cellphone;

    private String cityName;
}
