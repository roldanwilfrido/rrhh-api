package com.leantech.rrhh.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.CustomException;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.models.entities.Employee;
import com.leantech.rrhh.models.entities.Position;
import com.leantech.rrhh.repositories.EmployeeRepository;
import com.leantech.rrhh.repositories.PositionRepository;
import com.leantech.rrhh.utils.Const;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PositionServiceTests extends BaseUtils {

    private EmployeeRepository employeeRepositoryMock = mock(EmployeeRepository.class);
    private PositionRepository repo = mock(PositionRepository.class);

    private PositionService service;

    @Test
    void getAll() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        List<Position> list = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {});
        when(repo.findAll()).thenReturn(list);

        service = new PositionService(employeeRepositoryMock, repo);
        Assertions.assertThat(service.getAll().size() == 2);
        Assertions.assertThat(service.getAll().get(0).getId().equals(1));
    }

    @Test
    void add() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(repo.save(eq(position))).thenReturn(position);
        PositionDto dto = new PositionDto();
        dto.setName(position.getName());

        service = new PositionService(employeeRepositoryMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Position.class));
    }

    @Test
    void update_OK() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(repo.findById(position.getId())).thenReturn(Optional.of(position));
        when(repo.save(eq(position))).thenReturn(position);
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        dto.setName(position.getName());

        service = new PositionService(employeeRepositoryMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Position.class));
    }

    @Test
    void update_NotFoundId() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(repo.findById(position.getId())).thenReturn(Optional.empty());
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        dto.setName(position.getName());

        service = new PositionService(employeeRepositoryMock, repo);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.addOrUpdate(dto));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.OBJ_NOT_FOUND, Const.POSITION, dto.getId())));
        verify(repo, times(0)).save(any(Position.class));
    }

    @Test
    void update_NameAlreadyExists() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(repo.getByName(position.getName())).thenReturn(Optional.of(position));
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        dto.setName(position.getName());

        service = new PositionService(employeeRepositoryMock, repo);
        CustomException exception = assertThrows(CustomException.class, () ->
                service.addOrUpdate(dto));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.EXISTS_VALUE, "Name")));
        verify(repo, times(0)).save(any(Position.class));
    }

    @Test
    void remove() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(employeeRepositoryMock.findByPerson_Id(position.getId())).thenReturn(Collections.emptyList());
        when(repo.findById(position.getId())).thenReturn(Optional.of(position));
        doNothing().when(repo).delete(eq(position));

        service = new PositionService(employeeRepositoryMock, repo);
        service.remove(position.getId());
        verify(repo, times(1)).delete(any(Position.class));
    }

    @Test
    void remove_Exception() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_POSITIONS);
        Position position = mapper.readValue(bodyResponse, new TypeReference<List<Position>>() {}).get(0);
        when(employeeRepositoryMock.findByPerson_Id(position.getId())).thenReturn(Collections.singletonList(new Employee()));
        when(repo.findById(position.getId())).thenReturn(Optional.of(position));

        service = new PositionService(employeeRepositoryMock, repo);
        CustomException exception = assertThrows(CustomException.class, () ->
                service.remove(position.getId()));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.RELATION_EXISTS, Const.POSITION, position.getId())));
        verify(repo, times(0)).delete(any(Position.class));
    }
}
