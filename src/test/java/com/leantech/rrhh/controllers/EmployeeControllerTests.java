package com.leantech.rrhh.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeResponseDto;
import com.leantech.rrhh.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTests extends BaseUtils {
    @MockBean
    private EmployeeService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllEmployees() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        List<EmployeeResponseDto> list = mapper.readValue(bodyResponse, new TypeReference<List<EmployeeResponseDto>>() {});
        when(service.getAll(any(EmployeeDto.class))).thenReturn(list);
        mockMvc.perform(get("/employees")).andDo(print()).andExpect(content().string(mapper.writeValueAsString(list)));
    }

    @Test
    void getEmployeeById_OK() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        EmployeeResponseDto dto = mapper.readValue(bodyResponse, new TypeReference<List<EmployeeResponseDto>>() {}).get(0);
        when(service.getById(dto.getId())).thenReturn(dto);
        mockMvc.perform(get("/employees/" + dto.getId()))
                .andDo(print()).andExpect(content().string(mapper.writeValueAsString(dto)));
    }

    @Test
    void getEmployeeById_Exception() throws Exception {
        doThrow(new NotFoundException("")).when(service).getById(3);
        mockMvc.perform(get("/employees/3")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addEmployee() throws Exception {
        String body = getMockJsonFile(EMPLOYEE_BODY);
        EmployeeDto dto = mapper.readValue(body, EmployeeDto.class);
        doNothing().when(service).addOrUpdate(any(EmployeeDto.class));
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void updateEmployee_OK() throws Exception {
        String body = getMockJsonFile(EMPLOYEE_BODY);
        EmployeeDto dto = mapper.readValue(body, EmployeeDto.class);
        dto.setId(1);
        doNothing().when(service).addOrUpdate(any(EmployeeDto.class));
        mockMvc.perform(put("/employees/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void updateEmployee_Exception() throws Exception {
        String body = getMockJsonFile(EMPLOYEE_BODY);
        EmployeeDto dto = mapper.readValue(body, EmployeeDto.class);
        doNothing().when(service).addOrUpdate(any(EmployeeDto.class));
        mockMvc.perform(put("/employees/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmployee_Ok() throws Exception {
        String body = getMockJsonFile(EMPLOYEE_BODY);
        EmployeeDto dto = mapper.readValue(body, EmployeeDto.class);
        dto.setId(1);
        doNothing().when(service).remove(dto.getId());
        mockMvc.perform(delete("/employees/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_BadRequestException() throws Exception {
        String body = getMockJsonFile(EMPLOYEE_BODY);
        EmployeeDto dto = mapper.readValue(body, EmployeeDto.class);
        doNothing().when(service).remove(dto.getId());
        mockMvc.perform(delete("/employees/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isBadRequest());
    }
}
