package com.leantech.rrhh.services;

import com.leantech.rrhh.exceptions.CustomException;
import com.leantech.rrhh.exceptions.NotFoundException;
import com.leantech.rrhh.models.dto.PersonDto;
import com.leantech.rrhh.models.entities.Person;
import com.leantech.rrhh.repositories.PersonRepository;
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
public class PersonService {
    private PersonRepository repository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<PersonDto> getAll() {
        return repository.findAll().stream()
                .map(this::dtoBuilder)
                .collect(Collectors.toList());
    }

    public PersonDto getById(Integer id) {
        return dtoBuilder(checksById(id));
    }

    public Person checksById(Integer id) {
        Optional<Person> PersonOpt = repository.findById(id);
        if (PersonOpt.isEmpty())
            throw new NotFoundException(MessageFormat.format(Const.OBJ_NOT_FOUND, Const.PERSON, id));
        return PersonOpt.get();
    }

    public void checksByNameAndLastName(String name, String lastName) {
        if (repository.getByNameAndLastName(name, lastName).isPresent())
            throw new CustomException(MessageFormat.format(Const.EXISTS_VALUE, Const.PERSON));
    }

    public void addOrUpdate(PersonDto dto) {
        checksByNameAndLastName(dto.getName(), dto.getLastName());
        if (Objects.nonNull(dto.getId())) {
            checksById(dto.getId());
        }
        Person Person = builder(dto);
        repository.save(Person);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.PERSON));
    }

    public void remove(Integer id) {
        Person Person = checksById(id);
        repository.delete(Person);
        log.info(MessageFormat.format(Const.OPERATION_DONE, Const.PERSON));
    }

    private Person builder(PersonDto dto){
        Person obj = new Person();
        obj.setId(dto.getId());
        obj.setName(dto.getName());
        obj.setLastName(dto.getLastName());
        obj.setAddress(dto.getAddress());
        obj.setCellphone(dto.getCellphone());
        obj.setCityName(dto.getCityName());
        return obj;
    }

    public PersonDto dtoBuilder(Person person){
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setLastName(person.getLastName());
        dto.setAddress(person.getAddress());
        dto.setCellphone(person.getCellphone());
        dto.setCityName(person.getCityName());
        return dto;
    }
}
