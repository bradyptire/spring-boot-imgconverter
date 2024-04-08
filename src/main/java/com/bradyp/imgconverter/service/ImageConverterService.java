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

	private HashMap<String, byte[]> cache = new HashMap<>();

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
	 * Convert an image to a specified format.
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

	/**
	 * 
	 * Convert an image to a specified format asynchronously. The result is
	 * published to the {@link EventPublisherService}.
	 * 
	 * @param sourceImageUrl URL of the image to convert
	 * @param toFormat       Format to convert to
	 * @return The name of the converted file, which serves as the key for later
	 *         retrievel.
	 */
	@Async
	public void convertAsync(URL sourceImageUrl, String toFormat) throws UnsupportedFormatException {
		ImageConversionResult result = null;

		String fileName = buildFilename(sourceImageUrl, toFormat);
		try {
			convert(sourceImageUrl, toFormat);
			result = new ImageConversionResult(fileName, get(fileName));
		} catch (Exception e) {
			result = new ImageConversionResult(fileName, e.getMessage());
		} finally {
			eventPublisherService.publish(result);
		}
	}

	/**
	 * Retrieve image content.
	 * 
	 * @param fileName The image filename to retrieve. 
	 * @return The image content or null if not found.
	 */
	public byte[] get(String fileName) {
		byte[] bytes = cache.get(fileName);

		return bytes == null ? null : Arrays.copyOf(cache.get(fileName), bytes.length);
	}

	public String buildFilename(URL sourceImageUrl, String toFormat) {
		return String.format("%s.%s", sourceImageUrl.hashCode(), toFormat);
	}

}