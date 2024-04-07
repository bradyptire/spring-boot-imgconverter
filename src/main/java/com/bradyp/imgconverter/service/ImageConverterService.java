package com.bradyp.imgconverter.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bradyp.imgconverter.service.model.ImageConversionResult;

/**
 * Image converter services uses {@link javax.imageio.ImageIO} to convert
 * between a limited set of image formats.
 * 
 * This class implements a simple cache that acts as a store for converted
 * images In the real world, this cache would be implemented as by an external
 * data store.
 */
@Service
public class ImageConverterService {
	private static final String[] supportedFormats = { "jpg", "gif", "png", "bmp" };

	private List<String> supportedFormatsList = Arrays.asList(supportedFormats);

	private HashMap<String, byte[]> cache = new HashMap<String, byte[]>();

	private EventPublisherService eventPublisherService;

	@Autowired
	public ImageConverterService(EventPublisherService eventPublishService) {
		this.eventPublisherService = eventPublishService;
	}

	/**
	 * Get a list of supported image formats.
	 * 
	 * @return a list of formats.
	 */
	public String[] getSupportedFormats() {
		return supportedFormats;
	}

	/**
	 * Convert an image to a specified format. Converted images are stored in the
	 * cache.
	 * 
	 * @param sourceImageUrl URL of the image to convert
	 * @param toFormat       Format to convert to
	 * @return The name of the converted file, which serves as the cache key for
	 *         later retrievel.
	 * 
	 * @throws UnsupportedFormatException The requested format is not supported.
	 */
	public String convert(URL sourceImageUrl, String toFormat) throws UnsupportedFormatException {
		if (!supportedFormatsList.contains(toFormat)) {
			throw new UnsupportedFormatException(String.format("%s is not supported", toFormat));
		}

		String fileName = buildFilename(sourceImageUrl, toFormat);
		byte[] outputBytes = cache.get(fileName);
		if (outputBytes != null) {
			return fileName;
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try (InputStream openStream = sourceImageUrl.openStream()) {
			BufferedImage imageIn = ImageIO.read(openStream);

			boolean result = ImageIO.write(imageIn, toFormat, outputStream);
			outputStream.flush();
			outputStream.close();

			if (!result) {
				// Shouldn't happen
				throw new UnsupportedFormatException(String.format("%s is not supported", toFormat));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		cache.put(fileName, outputStream.toByteArray());

		return fileName;
	}

	@Async
	public void convertAsync(URL sourceImageUrl, String toFormat) throws UnsupportedFormatException {
		ImageConversionResult result = new ImageConversionResult();

		try {
			String fileName = convert(sourceImageUrl, toFormat);

			result.setSuccess(true);
			result.setContent(get(fileName));
			result.setFileName(fileName);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setFileName(buildFilename(sourceImageUrl, toFormat));
			result.setError(e.getMessage());
		} finally {
			eventPublisherService.publish(result);
		}
	}

	public byte[] get(String fileName) {
		return cache.get(fileName);
	}

	public String buildFilename(URL sourceImageUrl, String toFormat) {
		return String.format("%s.%s", sourceImageUrl.hashCode(), toFormat);
	}

}