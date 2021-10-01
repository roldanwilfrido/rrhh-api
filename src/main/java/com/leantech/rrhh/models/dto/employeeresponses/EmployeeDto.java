package com.leantech.rrhh.models.dto.employeeresponses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {
    private Integer id;

    @JsonIgnore
    private String name;

    private Integer person;

    private Integer position;

    private Integer salary;
}
