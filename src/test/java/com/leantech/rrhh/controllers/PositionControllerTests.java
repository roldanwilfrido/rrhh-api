package com.leantech.rrhh.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.services.PositionService;
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

@WebMvcTest(PositionController.class)
public class PositionControllerTests extends BaseUtils {
    @MockBean
    private PositionService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPositions() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        List<PositionDto> list = mapper.readValue(bodyResponse, new TypeReference<List<PositionDto>>() {});
        when(service.getAll()).thenReturn(list);
        mockMvc.perform(get("/positions")).andDo(print()).andExpect(content().string(mapper.writeValueAsString(list)));
    }

    @Test
    void getPositionById_OK() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        PositionDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PositionDto>>() {}).get(0);
        when(service.getById(dto.getId())).thenReturn(dto);
        mockMvc.perform(get("/positions/" + dto.getId()))
                .andDo(print()).andExpect(content().string(mapper.writeValueAsString(dto)));
    }

    @Test
    void getPositionById_Exception() throws Exception {
        doThrow(new NotFoundException("")).when(service).getById(3);
        mockMvc.perform(get("/positions/3")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addPosition() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        PositionDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PositionDto>>() {}).get(0);
        doNothing().when(service).addOrUpdate(any(PositionDto.class));
        mockMvc.perform(post("/positions")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void updatePosition() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        PositionDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PositionDto>>() {}).get(0);
        doNothing().when(service).addOrUpdate(any(PositionDto.class));
        mockMvc.perform(put("/positions/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void deletePosition() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        PositionDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PositionDto>>() {}).get(0);
        doNothing().when(service).remove(dto.getId());
        mockMvc.perform(delete("/positions/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }
}
