package com.bradyp.imgconverter.api.v1.model;

import java.net.URL;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Bean representing an image conversion request.
 */
public class ImageConversionRequest {
	@NotNull(message = "sourceImage is required")
	private URL sourceImage;

	@NotNull(message = "toFormat is required")
	@Size(min = 3, max = 3)
	private String toFormat;

	public URL getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(URL sourceImage) {
		this.sourceImage = sourceImage;
	}

	public String getToFormat() {
		return toFormat;
	}

	public void setToFormat(String toFormat) {
		this.toFormat = toFormat;
	}
}
