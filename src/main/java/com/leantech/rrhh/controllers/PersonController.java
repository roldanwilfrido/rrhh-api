package com.leantech.rrhh.controllers;

import com.leantech.rrhh.models.dto.PersonDto;
import com.leantech.rrhh.services.PersonService;
import com.leantech.rrhh.utils.Const;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

import static com.leantech.rrhh.utils.Util.getId;

@RequestMapping("/persons")
@RestController
@Log4j2
public class PersonController {
    private final PersonService service;

    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonDto>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDto> getById(@PathVariable(name = "id") String id) {
        return new ResponseEntity<>(service.getById(getId(id, Const.PERSON)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPerson(@RequestBody PersonDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Adding", Const.PERSON));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePerson(@PathVariable(name = "id") String id, @RequestBody PersonDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Updating", Const.PERSON));
        dto.setId(getId(id, Const.PERSON));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removePersonById(@PathVariable(name = "id") String id) {
        log.info(MessageFormat.format(Const.CALLING_API, "Removing", Const.PERSON));
        service.remove(getId(id, Const.PERSON));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
