package com.leantech.rrhh.services;

import com.leantech.rrhh.exceptions.CustomException;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeFullDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeResponseDto;
import com.leantech.rrhh.models.entities.Employee;
import com.leantech.rrhh.models.entities.Person;
import com.leantech.rrhh.models.entities.Position;
import com.leantech.rrhh.repositories.EmployeeRepository;
import com.leantech.rrhh.utils.Const;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EmployeeService {
    private PositionService positionService;
    private PersonService personService;
    private EmployeeRepository repository;

    @Autowired
    public EmployeeService(PositionService positionService, PersonService personService,
                           EmployeeRepository repository) {
        this.positionService = positionService;
        this.personService = personService;
        this.repository = repository;
    }

    public List<EmployeeResponseDto> getAll(EmployeeDto dto) {
        List<EmployeeResponseDto> list = new ArrayList<>();
        if (!Objects.equals(dto.getPosition(), -1) && !Objects.equals(dto.getName(), "-1")) {
            PositionDto positionDto = positionService.getById(dto.getPosition());
            List<Employee> employees = repository.getByPositionAndPersonName(dto.getPosition(), dto.getName());
            EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
            employeeResponseDto.setId(positionDto.getId());
            employeeResponseDto.setName(positionDto.getName());
            if (!employees.isEmpty()) {
                employeeResponseDto.setEmployees(
                        employees.stream().map(this::dtoBuilder)
                                .collect(Collectors.toList()));
            }
            list.add(employeeResponseDto);
        } else if (!Objects.equals(dto.getPosition(), -1)) {
            PositionDto positionDto = positionService.getById(dto.getPosition());
            List<Employee> employees = repository.getByPosition(dto.getPosition());
            EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
            employeeResponseDto.setId(positionDto.getId());
            employeeResponseDto.setName(positionDto.getName());
            employeeResponseDto.setEmployees(
                    employees.stream().map(this::dtoBuilder)
                            .collect(Collectors.toList()));
            list.add(employeeResponseDto);
        } else if (!Objects.equals(dto.getName(), "-1")) {
            positionService.getAll().forEach(positionDto -> {
                List<Employee> employees = repository.getByPositionAndPersonName(dto.getPosition(), dto.getName());
                EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
                employeeResponseDto.setId(positionDto.getId());
                employeeResponseDto.setName(positionDto.getName());
                if (!employees.isEmpty()) {
                    employeeResponseDto.setEmployees(
                            employees.stream().map(this::dtoBuilder)
                                    .collect(Collectors.toList()));
                }
                list.add(employeeResponseDto);
            });
        } else {
            positionService.getAll().forEach(positionDto -> {
                List<Employee> employees = repository.getByPosition(positionDto.getId());
                EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
                employeeResponseDto.setId(positionDto.getId());
                employeeResponseDto.setName(positionDto.getName());
                if (!employees.isEmpty()) {
                    employeeResponseDto.setEmployees(
                            employees.stream().map(this::dtoBuilder)
                                    .collect(Collectors.toList()));
                }
                list.add(employeeResponseDto);
            });
        }

        return list;
    }

    public EmployeeResponseDto getById(Integer id) {
        Employee employee = checksById(id);
        return dtoComplexBuilder(employee.getPosition(), Collections.singletonList(employee));
    }

    public Employee checksById(Integer id) {
        Optional<Employee> EmployeeOpt = repository.findById(id);
        if (EmployeeOpt.isEmpty())
            throw new NotFoundException(MessageFormat.format(Const.OBJ_NOT_FOUND, Const.EMPLOYEE, id));
        return EmployeeOpt.get();
    }

    public void checkIfAlreadyExists(Integer position, Integer person) {
        Optional<Employee> employeeOpt = repository.getByPositionAndPerson(position, person);
        if (employeeOpt.isPresent())
            throw new CustomException(MessageFormat.format(Const.EXISTS_VALUE, Const.EMPLOYEE));
    }

    public void addOrUpdate(EmployeeDto dto) {
        if (hasNoChangesOrAlreadyExists(dto)) return;
        Employee Employee = builder(dto);
        repository.save(Employee);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.EMPLOYEE));
    }

    public void remove(Integer id) {
        Employee Employee = checksById(id);
        repository.delete(Employee);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.EMPLOYEE));
    }

    private Employee builder(EmployeeDto dto) {
        Position position = positionService.checksById(dto.getPosition());
        Person person = personService.checksById(dto.getPerson());

        Employee obj = new Employee();
        obj.setId(dto.getId());
        obj.setSalary(dto.getSalary());
        obj.setPosition(position);
        obj.setPerson(person);
        return obj;
    }

    private EmployeeFullDto dtoBuilder(Employee employee){
        EmployeeFullDto dto = new EmployeeFullDto();
        dto.setId(employee.getId());
        dto.setSalary(employee.getSalary());
        dto.setPerson(personService.dtoBuilder(employee.getPerson()));
        return dto;
    }

    private EmployeeResponseDto dtoComplexBuilder(Position position, List<Employee> employeeList){
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setEmployees(employeeList.stream().map(this::dtoBuilder).collect(Collectors.toList()));
        return dto;
    }

    private boolean hasNoChangesOrAlreadyExists(EmployeeDto dto) {
        if (Objects.nonNull(dto.getId())) {
            Employee employee = checksById(dto.getId());
            return Objects.equals(employee.getPosition().getId(), dto.getPosition()) &&
                    Objects.equals(employee.getPerson().getId(), dto.getPerson());
        }
        checkIfAlreadyExists(dto.getPosition(), dto.getPerson());
        return false;
    }
}
