package com.bradyp.imgconverter.api.v1;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bradyp.imgconverter.api.v1.model.ImageConversionRequest;
import com.bradyp.imgconverter.api.v1.model.ImageConversionResponse;
import com.bradyp.imgconverter.api.v1.model.UnsupportedFormatExceptionResponse;
import com.bradyp.imgconverter.service.ImageConverterService;
import com.bradyp.imgconverter.service.UnsupportedFormatException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for image conversion service.
 */
@RestController
@RequestMapping(ImageConverterController.BASE_PATH)
public class ImageConverterController {
	public static final String BASE_PATH = "/api/v1/images/conversions/";

	private ImageConverterService imageConverterService;
	
	private Logger logger = LoggerFactory.getLogger(ImageConverterController.class);

	@Autowired
	public ImageConverterController(ImageConverterService imageConverterService) {
		this.imageConverterService = imageConverterService;
	}

	/**
	 * Retrieve a list of support image formats.
	 * 
	 * @return a list for supported formats.
	 */
	@GetMapping(value = "/formats", produces = MediaType.APPLICATION_JSON_VALUE)
	public String[] supportedFormats() {
		return imageConverterService.getSupportedFormats();
	}

	/**
	 * Accepts a request containing a url to an image, converts that image to the
	 * requested format and returns a path to where the converted image can be
	 * retrieved.
	 * 
	 * @param request The request object.
	 */
	@PostMapping("/convert")
	@ResponseStatus(HttpStatus.CREATED)
	public ImageConversionResponse convert(@RequestBody @Validated ImageConversionRequest request) {
		try {
			String fileName = imageConverterService.convert(request.getSourceImage(), request.getToFormat());
			return new ImageConversionResponse(request.getToFormat(), buildFilePath(fileName));
		} catch (UnsupportedFormatException e) {
			throw new UnsupportedFormatExceptionResponse(e);
		}
	}

	/**
	 * Accepts a request containing a url to an image, converts that image to the
	 * requested format and publishes the result to an event streaming service.
	 * 
	 * @param request The request object.
	 */
	@PostMapping("/convert/async")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ImageConversionResponse convertASync(@RequestBody @Validated ImageConversionRequest request) {
		logger.info("convertASync {}", Thread.currentThread().getName());		

		String fileName = imageConverterService.buildFilename(request.getSourceImage(), request.getToFormat());

		try {
			imageConverterService.convertAsync(request.getSourceImage(), request.getToFormat());
		} catch (UnsupportedFormatException e) {
			throw new UnsupportedFormatExceptionResponse(e);
		}

		return new ImageConversionResponse(request.getToFormat(), buildFilePath(fileName));
	}

	/**
	 * Retrieve a previously converted image.
	 * 
	 * @param response The HTTP response to which the image content will be written.
	 * @param imgfile  The name of the file to be retrieved.
	 */
	@GetMapping(value = "{imgfile}")
	public void get(final HttpServletResponse response, @PathVariable("imgfile") String imgfile) {
		byte[] bs = imageConverterService.get(imgfile);

		if (bs == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s not found", imgfile));
		}

		response.setHeader("Content-Disposition", String.format("inline; filename=%s", imgfile));

		try {
			IOUtils.copy(new ByteArrayInputStream(bs), response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, null, e);
		}

	}

	private String buildFilePath(String fileName) {
		return String.format("%s%s", BASE_PATH, fileName);
	}
}