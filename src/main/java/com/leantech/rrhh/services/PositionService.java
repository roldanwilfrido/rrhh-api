package com.leantech.rrhh.services;

import com.leantech.rrhh.exceptions.CustomException;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.models.entities.Position;
import com.leantech.rrhh.repositories.EmployeeRepository;
import com.leantech.rrhh.repositories.PositionRepository;
import com.leantech.rrhh.utils.Const;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PositionService {
    private EmployeeRepository employeeRepository;
    private PositionRepository repository;

    @Autowired
    public PositionService(EmployeeRepository employeeRepository, PositionRepository repository) {
        this.employeeRepository = employeeRepository;
        this.repository = repository;
    }

    public List<PositionDto> getAll() {
        return repository.findAll().stream()
                .map(this::dtoBuilder)
                .collect(Collectors.toList());
    }

    public PositionDto getById(Integer id) {
        return dtoBuilder(checksById(id));
    }

    public Position checksById(Integer id) {
        Optional<Position> positionOpt = repository.findById(id);
        if (positionOpt.isEmpty())
            throw new NotFoundException(MessageFormat.format(Const.OBJ_NOT_FOUND, Const.POSITION, id));
        return positionOpt.get();
    }

    public void checksByName(String name) {
        if (repository.getByName(name).isPresent())
            throw new CustomException(MessageFormat.format(Const.EXISTS_VALUE, "Name"));
    }

    public void addOrUpdate(PositionDto dto) {
        checksByName(dto.getName());
        if (Objects.nonNull(dto.getId())) {
            checksById(dto.getId());
        }
        Position position = builder(dto);
        repository.save(position);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.POSITION));
    }

    public void remove(Integer id) {
        Position position = checksById(id);
        if (employeeRepository.findByPerson_Id(id).size() > 0) {
            throw new CustomException(MessageFormat.format(Const.RELATION_EXISTS, Const.POSITION, id));
        }

        repository.delete(position);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.POSITION));
    }

    private Position builder(PositionDto dto){
        Position obj = new Position();
        obj.setId(dto.getId());
        obj.setName(dto.getName());
        return obj;
    }

    private PositionDto dtoBuilder(Position position){
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        dto.setName(position.getName());
        return dto;
    }
}
