package com.hangoutz.app.service;

import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilService {

    public static void checkEmailIsValid(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) throw new BadRequestException(ExceptionMessage.INVALID_EMAIL);
    }
}
