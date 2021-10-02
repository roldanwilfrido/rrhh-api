package com.leantech.rrhh.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.CustomException;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PersonDto;
import com.leantech.rrhh.models.entities.Employee;
import com.leantech.rrhh.models.entities.Person;
import com.leantech.rrhh.repositories.EmployeeRepository;
import com.leantech.rrhh.repositories.PersonRepository;
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

public class PersonServiceTests extends BaseUtils {
    
    private EmployeeRepository employeeRepositoryMock = mock(EmployeeRepository.class);
    private PersonRepository repo = mock(PersonRepository.class);

    private PersonService service;

    @Test
    void getAll() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        List<Person> list = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {});
        when(repo.findAll()).thenReturn(list);

        service = new PersonService(employeeRepositoryMock, repo);
        Assertions.assertThat(service.getAll().size() == 2);
        Assertions.assertThat(service.getAll().get(0).getId().equals(1));
    }

    @Test
    void add() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(repo.save(eq(person))).thenReturn(person);
        PersonDto dto = new PersonDto();
        dto.setName(person.getName());

        service = new PersonService(employeeRepositoryMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Person.class));
    }

    @Test
    void update_OK() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(repo.findById(person.getId())).thenReturn(Optional.of(person));
        when(repo.save(eq(person))).thenReturn(person);
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());

        service = new PersonService(employeeRepositoryMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Person.class));
    }

    @Test
    void update_NotFoundId() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(repo.findById(person.getId())).thenReturn(Optional.empty());
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());

        service = new PersonService(employeeRepositoryMock, repo);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.addOrUpdate(dto));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.OBJ_NOT_FOUND, Const.PERSON, dto.getId())));
        verify(repo, times(0)).save(any(Person.class));
    }

    @Test
    void update_PersonAlreadyExists() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(repo.getByNameAndLastName(person.getName(), person.getLastName())).thenReturn(Optional.of(person));
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setLastName(person.getLastName());

        service = new PersonService(employeeRepositoryMock, repo);
        CustomException exception = assertThrows(CustomException.class, () ->
                service.addOrUpdate(dto));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.EXISTS_VALUE, Const.PERSON)));
        verify(repo, times(0)).save(any(Person.class));
    }

    @Test
    void remove() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(employeeRepositoryMock.findByPosition_Id(person.getId())).thenReturn(Collections.emptyList());
        when(repo.findById(person.getId())).thenReturn(Optional.of(person));
        doNothing().when(repo).delete(eq(person));

        service = new PersonService(employeeRepositoryMock, repo);
        service.remove(person.getId());
        verify(repo, times(1)).delete(any(Person.class));
    }

    @Test
    void remove_Exception() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_PERSON);
        Person person = mapper.readValue(bodyResponse, new TypeReference<List<Person>>() {}).get(0);
        when(employeeRepositoryMock.findByPosition_Id(person.getId())).thenReturn(Collections.singletonList(new Employee()));
        when(repo.findById(person.getId())).thenReturn(Optional.of(person));
        doNothing().when(repo).delete(eq(person));

        service = new PersonService(employeeRepositoryMock, repo);
        CustomException exception = assertThrows(CustomException.class, () ->
                service.remove(person.getId()));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.RELATION_EXISTS, Const.PERSON, person.getId())));
        verify(repo, times(0)).delete(any(Person.class));
    }
}
