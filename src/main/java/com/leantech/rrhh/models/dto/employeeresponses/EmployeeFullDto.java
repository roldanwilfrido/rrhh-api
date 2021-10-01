package com.leantech.rrhh.models.dto.employeeresponses;

import com.leantech.rrhh.models.dto.PersonDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFullDto {
    private Integer id;

    private Integer salary;

    private PersonDto person;
}
