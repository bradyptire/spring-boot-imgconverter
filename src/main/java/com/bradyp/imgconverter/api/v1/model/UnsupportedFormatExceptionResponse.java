package com.bradyp.imgconverter.api.v1.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Bad request -> requested format is not supported.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedFormatExceptionResponse extends RuntimeException {

	private static final long serialVersionUID = 7759954201538223435L;

	public UnsupportedFormatExceptionResponse(Exception e) {
		super(e);
	}
}
