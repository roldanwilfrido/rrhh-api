package com.leantech.rrhh.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PersonDto;
import com.leantech.rrhh.services.PersonService;
import com.leantech.rrhh.services.PersonService;
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

@WebMvcTest(PersonController.class)
public class PersonControllerTests extends BaseUtils {
    @MockBean
    private PersonService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPersons() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        List<PersonDto> list = mapper.readValue(bodyResponse, new TypeReference<List<PersonDto>>() {});
        when(service.getAll()).thenReturn(list);
        mockMvc.perform(get("/persons")).andDo(print()).andExpect(content().string(mapper.writeValueAsString(list)));
    }

    @Test
    void getPersonById_OK() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        PersonDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PersonDto>>() {}).get(0);
        when(service.getById(dto.getId())).thenReturn(dto);
        mockMvc.perform(get("/persons/" + dto.getId()))
                .andDo(print()).andExpect(content().string(mapper.writeValueAsString(dto)));
    }

    @Test
    void getPersonById_Exception() throws Exception {
        doThrow(new NotFoundException("")).when(service).getById(3);
        mockMvc.perform(get("/persons/3")
                .contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addPerson() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        PersonDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PersonDto>>() {}).get(0);
        doNothing().when(service).addOrUpdate(any(PersonDto.class));
        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void updatePerson() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        PersonDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PersonDto>>() {}).get(0);
        doNothing().when(service).addOrUpdate(any(PersonDto.class));
        mockMvc.perform(put("/persons/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void deletePerson() throws Exception {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        PersonDto dto = mapper.readValue(bodyResponse, new TypeReference<List<PersonDto>>() {}).get(0);
        doNothing().when(service).remove(dto.getId());
        mockMvc.perform(delete("/persons/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isNoContent());
    }
}
