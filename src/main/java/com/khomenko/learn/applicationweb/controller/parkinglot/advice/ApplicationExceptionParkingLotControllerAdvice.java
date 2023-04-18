/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.parkinglot.advice;

import com.khomenko.learn.applicationweb.utils.exception.ApplicationBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionParkingLotControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ApplicationBusinessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String applicationBusinessExceptionHandler(ApplicationBusinessException exc) {
        return exc.getMessage();
    }
}
