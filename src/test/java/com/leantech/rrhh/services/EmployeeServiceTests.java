package com.leantech.rrhh.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leantech.rrhh.BaseUtils;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeResponseDto;
import com.leantech.rrhh.models.entities.Employee;
import com.leantech.rrhh.models.entities.Person;
import com.leantech.rrhh.models.entities.Position;
import com.leantech.rrhh.repositories.EmployeeRepository;
import com.leantech.rrhh.utils.Const;
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

public class EmployeeServiceTests extends BaseUtils {

    private EmployeeRepository repo = mock(EmployeeRepository.class);
    private PositionService positionServiceMock = mock(PositionService.class);
    private PersonService personServiceMock = mock(PersonService.class);

    private EmployeeService service;

    @Test
    void getAll_NoCriteria() throws JsonProcessingException {
        EmployeeDto dto = new EmployeeDto();
        dto.setPosition(-1);
        dto.setName("-1");
        List<PositionDto> positions = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {});
        List<Employee> devEmployees = mapper.readValue(getMockJsonFile(EMPLOYEE_POSITION_DEV), new TypeReference<List<Employee>>() {});
        List<Employee> qaEmployees = mapper.readValue(getMockJsonFile(EMPLOYEE_POSITION_DEV), new TypeReference<List<Employee>>() {});
        when(positionServiceMock.getAll()).thenReturn(positions);
        when(repo.getByPosition(1)).thenReturn(devEmployees);
        when(repo.getByPosition(2)).thenReturn(qaEmployees);

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        List<EmployeeResponseDto> result = service.getAll(dto);
        assertTrue(Objects.equals(result.get(0).getName(), "dev"));
        assertTrue(Objects.equals(result.get(1).getName(), "qa"));
        assertTrue(result.size() == 2);
    }

    @Test
    void getAll_PositionDev() throws JsonProcessingException {
        EmployeeDto dto = new EmployeeDto();
        dto.setPosition(1);
        dto.setName("-1");
        PositionDto positionDto = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {}).get(0);
        List<Employee> devEmployees = mapper.readValue(getMockJsonFile(EMPLOYEE_POSITION_DEV), new TypeReference<List<Employee>>() {});
        when(positionServiceMock.getById(dto.getPosition())).thenReturn(positionDto);
        when(repo.getByPosition(dto.getPosition())).thenReturn(devEmployees);

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        List<EmployeeResponseDto> result = service.getAll(dto);
        assertTrue(Objects.equals(result.get(0).getName(), "dev"));
        assertTrue(result.size() == 1);
    }

    @Test
    void getAll_CriteriaPersonNameWithA() throws JsonProcessingException {
        EmployeeDto dto = new EmployeeDto();
        dto.setPosition(-1);
        dto.setName("a");
        List<PositionDto> positions = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {});
        PositionDto positionDto = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {}).get(0);
        when(positionServiceMock.getAll()).thenReturn(positions);
        when(positionServiceMock.getById(dto.getPosition())).thenReturn(positionDto);
        Employee devEmployeeCriteria = getEmployee();

        when(repo.findByPosition_IdAndPerson_NameContainingOrderBySalaryDesc(1, dto.getName())).thenReturn(Collections.singletonList(devEmployeeCriteria));
        when(repo.findByPosition_IdAndPerson_NameContainingOrderBySalaryDesc(2, dto.getName())).thenReturn(Collections.emptyList());

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        List<EmployeeResponseDto> result = service.getAll(dto);
        assertTrue(Objects.equals(result.get(0).getName(), "dev"));
        assertTrue(result.size() == 2);
        assertTrue(result.get(0).getEmployees().size() == 1);
        assertTrue(result.get(0).getEmployees().get(0).getSalary() == 2000);
    }

    @Test
    void getAll_PositionDev_CriteriaPersonNameWithA() throws JsonProcessingException {
        EmployeeDto dto = new EmployeeDto();
        dto.setPosition(1);
        dto.setName("a");
        List<PositionDto> positions = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {});
        PositionDto positionDto = mapper.readValue(getMockJsonFile(ALL_POSITIONS), new TypeReference<List<PositionDto>>() {}).get(0);
        when(positionServiceMock.getAll()).thenReturn(positions);
        when(positionServiceMock.getById(dto.getPosition())).thenReturn(positionDto);
        Employee devEmployeeCriteria = getEmployee();

        when(repo.findByPosition_IdAndPerson_NameContainingOrderBySalaryDesc(1, dto.getName())).thenReturn(Collections.singletonList(devEmployeeCriteria));
        when(repo.findByPosition_IdAndPerson_NameContainingOrderBySalaryDesc(2, dto.getName())).thenReturn(Collections.emptyList());

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        List<EmployeeResponseDto> result = service.getAll(dto);
        assertTrue(Objects.equals(result.get(0).getName(), "dev"));
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getEmployees().size() == 1);
        assertTrue(result.get(0).getEmployees().get(0).getSalary() == 2000);
    }

    @Test
    void add() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_EMPLOYEES);
        Employee employee = mapper.readValue(bodyResponse, new TypeReference<List<Employee>>() {}).get(0);
        when(repo.save(eq(employee))).thenReturn(employee);
        EmployeeDto dto = new EmployeeDto();
        dto.setPosition(1);
        dto.setPerson(1);
        dto.setSalary(employee.getSalary());

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Employee.class));
    }

    @Test
    void update_OK() {
        Employee employee = getEmployee();
        when(repo.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(repo.save(eq(employee))).thenReturn(employee);
        EmployeeDto dto = new EmployeeDto();
        dto.setId(1);
        dto.setPosition(2);
        dto.setPerson(1);
        dto.setSalary(employee.getSalary());

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(1)).save(any(Employee.class));
    }

    @Test
    void update_NotFoundId() {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(5);
        dto.setPosition(2);
        dto.setPerson(1);
        dto.setSalary(2000);
        when(repo.findById(5)).thenReturn(Optional.empty());

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.addOrUpdate(dto));
        assertTrue(Objects.equals(exception.getMessage(),
                MessageFormat.format(Const.OBJ_NOT_FOUND, Const.EMPLOYEE, dto.getId())));
        verify(repo, times(0)).save(any(Employee.class));
    }


    @Test
    void update_EmployeeNoChanges() {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(1);
        dto.setPosition(1);
        dto.setPerson(1);
        dto.setSalary(2000);
        when(repo.findById(1)).thenReturn(Optional.of(getEmployee()));

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        service.addOrUpdate(dto);
        verify(repo, times(0)).save(any(Employee.class));
    }

    @Test
    void remove() throws JsonProcessingException {
        String bodyResponse = getMockJsonFile(ALL_EMPLOYEES);
        Employee employee = mapper.readValue(bodyResponse, new TypeReference<List<Employee>>() {}).get(0);
        when(repo.findById(employee.getId())).thenReturn(Optional.of(employee));
        doNothing().when(repo).delete(eq(employee));

        service = new EmployeeService(positionServiceMock, personServiceMock, repo);
        service.remove(employee.getId());
        verify(repo, times(1)).delete(any(Employee.class));
    }

    private Employee getEmployee() {
        Employee devEmployeeCriteria = new Employee();
        devEmployeeCriteria.setId(1);
        Position position = new Position();
        position.setId(1);
        position.setName("dev");
        devEmployeeCriteria.setPosition(position);
        devEmployeeCriteria.setSalary(2000);
        Person person = new Person();
        person.setId(1);
        person.setName("Camilo");
        person.setLastName("ruiz");
        person.setAddress("cra");
        person.setCellphone("124");
        person.setCityName("Medellin");
        devEmployeeCriteria.setPerson(person);
        return devEmployeeCriteria;
    }
}
