package com.bradyp.imgconverter.service;

public class UnsupportedFormatException extends RuntimeException {

	private static final long serialVersionUID = -490172308456621661L;

	public UnsupportedFormatException(String msg) {
		super(msg);
	}
}
