package com.leantech.rrhh.utils;

import com.leantech.rrhh.exceptions.CustomException;

import java.text.MessageFormat;

public class Util {

    public static Integer getId(String id, String who) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException nfe) {
            throw new CustomException(MessageFormat.format(Const.NUMBER_ID_ERROR, who));
        }
    }
}
