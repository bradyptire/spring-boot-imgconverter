package com.bradyp.imgconverter.spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bradyp.imgconverter.service.UnsupportedFormatException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(UnsupportedFormatException.class)
	public ProblemDetail handle(UnsupportedFormatException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
