package com.leantech.rrhh.models.dto.employeeresponses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EmployeeResponseDto {
    private Integer id;

    private String name;

    private List<EmployeeFullDto> employees = new ArrayList<>();
}
