package com.my.jobsearcher.view.errors;

import com.my.jobsearcher.store.dto.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class CustomErrorResponse extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorDto> handleBadRequestExc(Exception e){
        System.out.println("bad request");
        return new ResponseEntity<>(new ErrorDto(HttpStatus.BAD_REQUEST,
                "bad request",
                Collections.emptyList()), HttpStatus.BAD_REQUEST);
    }
}
