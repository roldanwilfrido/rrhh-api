package com.leantech.rrhh.controllers;

import com.leantech.rrhh.models.dto.PositionDto;
import com.leantech.rrhh.services.PositionService;
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

@RequestMapping("/positions")
@RestController
@Log4j2
public class PositionController {
    private final PositionService service;

    @Autowired
    public PositionController(PositionService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PositionDto>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PositionDto> getById(@PathVariable(name = "id") String id) {
        return new ResponseEntity<>(service.getById(getId(id, Const.POSITION)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPosition(@RequestBody PositionDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Adding", Const.POSITION));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePosition(@PathVariable(name = "id") String id, @RequestBody PositionDto dto) {
        log.info(MessageFormat.format(Const.CALLING_API, "Updating", Const.POSITION));
        dto.setId(getId(id, Const.POSITION));
        service.addOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removePositionById(@PathVariable(name = "id") String id) {
        log.info(MessageFormat.format(Const.CALLING_API, "Removing", Const.POSITION));
        service.remove(getId(id, Const.POSITION));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
