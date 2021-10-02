package com.leantech.rrhh;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leantech.rrhh.controllers.PositionController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BaseUtils {
    public static final String ALL_POSITIONS = "positions_all.json";
    public static final String ALL_PERSON = "persons_all.json";
    public static final String ALL_EMPLOYEES = "employees_all.json";
    public static final String EMPLOYEE_BODY = "employee_body.json";
    public static final String EMPLOYEE_POSITION_DEV = "employees_dev.json";

    public ObjectMapper mapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public String getMockJsonFile(String file) {
        try {
            return Files.lines(Paths.get("src", "test", "resources", file))
                    .collect(Collectors.joining());
        } catch (IOException ex) {
            Logger.getLogger(PositionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
