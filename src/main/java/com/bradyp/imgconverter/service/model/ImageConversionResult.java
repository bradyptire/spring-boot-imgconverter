package com.bradyp.imgconverter.service.model;

/**
 * Class represents the result of an asynchronous image conversion.
 */
public class ImageConversionResult {
	private boolean success;

	private String error;

	private String fileName;

	private byte[] content;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
