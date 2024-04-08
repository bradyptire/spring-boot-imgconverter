package com.bradyp.imgconverter.service.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Class represents the result of an asynchronous image conversion.
 */
public class ImageConversionResult {
	private final boolean success;

	private final String error;

	private final String fileName;

	private final byte[] content;

	public ImageConversionResult(String fileName, byte[] content) {
		super();
		this.success = true;
		this.fileName = fileName;
		this.content = content;
		this.error = null;
	}

	public ImageConversionResult(String fileName, String error) {
		super();
		this.success = false;
		this.error = error;
		this.fileName = fileName;
		this.content = null;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getContent() {
		return content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(content);
		result = prime * result + Objects.hash(error, fileName, success);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageConversionResult other = (ImageConversionResult) obj;
		return Arrays.equals(content, other.content) && Objects.equals(error, other.error)
				&& Objects.equals(fileName, other.fileName) && success == other.success;
	}
}
