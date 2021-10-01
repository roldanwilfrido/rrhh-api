package com.leantech.rrhh.controllers;

import com.leantech.rrhh.models.dto.employeeresponses.EmployeeDto;
import com.leantech.rrhh.models.dto.employeeresponses.EmployeeResponseDto;
import com.leantech.rrhh.services.EmployeeService;
import com.leantech.rrhh.utils.Const;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static com.leantech.rrhh.utils.Util.getId;

@RequestMapping("/employees")
@RestController
@Log4j2
public class EmployeeController {
    private final EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeResponseDto>> getAll(
            @RequestParam(required = false) String position, @RequestParam(required = false) String name) {
        EmployeeDto dto = new EmployeeDto();
        if ((Objects.nonNull(position) && position.length() > 0)) {
            dto.setPosition(getId(position, "Position"));
        } else {
            dto.setPosition(-1);
        }
        dto.setName(Objects.nonNull(name)? name: "-1");
        return new ResponseEntity<>(service.getAll(dto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable(name = "id") String id) {
        return new ResponseEntity<>(service.getById(getId(id, Const.EMPLOYEE)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addEmployee(@RequestBody EmployeeDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Adding", Const.EMPLOYEE));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateEmployee(@PathVariable(name = "id") String id, @RequestBody EmployeeDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Updating", Const.EMPLOYEE));
        dto.setId(getId(id, Const.EMPLOYEE));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeEmployeeById(@PathVariable(name = "id") String id) {
        log.info(MessageFormat.format(Const.CALLING_API, "Removing", Const.EMPLOYEE));
        service.remove(getId(id, Const.EMPLOYEE));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
