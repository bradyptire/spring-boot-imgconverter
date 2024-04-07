package com.bradyp.api.imgconverter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradyp.imgconverter.service.EventPublisherService;
import com.bradyp.imgconverter.service.ImageConverterService;
import com.bradyp.imgconverter.service.UnsupportedFormatException;
import com.bradyp.imgconverter.service.model.ImageConversionResult;

/**
 * Test scenarios for {@link ImageConverterService}.
 * 
 * This class converts between the various supported formats using images stored
 * in src/test/resources.
 */
@ExtendWith(MockitoExtension.class)
public class ImageConverterServiceTest {
	@Mock
	private EventPublisherService eventPublisherService;

	@InjectMocks
	private ImageConverterService cut;

	@Captor
	ArgumentCaptor<ImageConversionResult> asyncResultCaptor;

	@DisplayName("Test conversion of gif to jpg")
	@Test
	public void whenConvertGifToJpgThenSuccess() throws IOException {
		URL fileToConvert = getImageUrl("for-gif-tests", "gif-test.gif");
		URL fileToCompare = getImageUrl("for-gif-tests", "jpg.jpg");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "jpg");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.jpg", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of gif to png")
	@Test
	public void whenConvertGifToPngThenSuccess() {
		URL fileToConvert = getImageUrl("for-gif-tests", "gif-test.gif");
		URL fileToCompare = getImageUrl("for-gif-tests", "png.png");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "png");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.png", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of gif to bmp")
	@Test
	public void whenConvertGifToBmpThenSuccess() {
		URL fileToConvert = getImageUrl("for-gif-tests", "gif-test.gif");
		URL fileToCompare = getImageUrl("for-gif-tests", "bmp.bmp");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "bmp");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.bmp", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of jpg to gif")
	@Test
	public void whenConvertJpgToGifThenSuccess() {
		URL fileToConvert = getImageUrl("for-jpg-tests", "jpg-test.jpg");
		URL fileToCompare = getImageUrl("for-jpg-tests", "gif.gif");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "gif");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.gif", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of jpg to png")
	@Test
	public void whenConvertJpgToPngThenSuccess() {
		URL fileToConvert = getImageUrl("for-jpg-tests", "jpg-test.jpg");
		URL fileToCompare = getImageUrl("for-jpg-tests", "png.png");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "png");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.png", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of jpg to bmp")
	@Test
	public void whenConvertJpgToBmpThenSuccess() {
		URL fileToConvert = getImageUrl("for-jpg-tests", "jpg-test.jpg");
		URL fileToCompare = getImageUrl("for-jpg-tests", "bmp.bmp");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "bmp");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.bmp", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of bmp to gif")
	@Test
	public void whenConvertBmpToGifThenSuccess() {
		URL fileToConvert = getImageUrl("for-bmp-tests", "bmp-test.bmp");
		URL fileToCompare = getImageUrl("for-bmp-tests", "gif.gif");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "gif");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.gif", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of bmp to png")
	@Test
	public void whenConvertBmpToPngThenSuccess() {
		URL fileToConvert = getImageUrl("for-bmp-tests", "bmp-test.bmp");
		URL fileToCompare = getImageUrl("for-bmp-tests", "png.png");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "png");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.png", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test conversion of bmp to jpg")
	@Test
	public void whenConvertBmpToJpgThenSuccess() {
		URL fileToConvert = getImageUrl("for-bmp-tests", "bmp-test.bmp");
		URL fileToCompare = getImageUrl("for-bmp-tests", "jpg.jpg");
		BufferedImage expectedImage = loadImage(fileToCompare);

		String convertedFileName = cut.convert(fileToConvert, "jpg");
		byte[] actualImageBytes = cut.get(convertedFileName);

		assertThat(convertedFileName).isEqualTo(String.format("%s.jpg", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, actualImageBytes);
	}

	@DisplayName("Test Unsupported Format")
	@Test
	public void whenConvertToInvalidFormatThenUnsupportedFormatException() {
		URL fileToConvert = getImageUrl("for-bmp-tests", "bmp-test.bmp");
		assertThrows(UnsupportedFormatException.class, () -> {
			cut.convert(fileToConvert, "svg");
		});
	}

	@DisplayName("Test convert async happy path")
	@Test
	public void whenConvertAsyncThenSuccess() {
		URL fileToConvert = getImageUrl("for-gif-tests", "gif-test.gif");
		URL fileToCompare = getImageUrl("for-gif-tests", "jpg.jpg");
		BufferedImage expectedImage = loadImage(fileToCompare);

		cut.convertAsync(fileToConvert, "jpg");

		verify(eventPublisherService).publish(asyncResultCaptor.capture());
		ImageConversionResult conversionResult = asyncResultCaptor.getValue();
		assertThat(conversionResult.isSuccess());
		assertThat(conversionResult.getFileName()).isEqualTo(String.format("%s.jpg", fileToConvert.hashCode()));
		assertImagesEqual(expectedImage, conversionResult.getContent());
	}

	@DisplayName("Test convert async Unsupported Format")
	@Test
	public void whenConvertAsyncToInvalidFormatThenUnsupportedFormatException() {
		URL fileToConvert = getImageUrl("for-gif-tests", "gif-test.gif");

		cut.convertAsync(fileToConvert, "svg");

		verify(eventPublisherService).publish(asyncResultCaptor.capture());
		ImageConversionResult conversionResult = asyncResultCaptor.getValue();
		assertThat(conversionResult.isSuccess()).isFalse();
		assertThat(conversionResult.getError()).isNotEmpty();
	}

	@DisplayName("Test get supported formats")
	@Test
	public void whenGetSupportedFormatsThenExpectedSupportedFormatsReturned() {
		String[] supportedFormats = cut.getSupportedFormats();

		assertThat(Arrays.asList(supportedFormats))
				.hasSameElementsAs((Arrays.asList(new String[] { "jpg", "gif", "png", "bmp" })));
	}

	private void assertImagesEqual(BufferedImage expected, BufferedImage actual) {
		if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
			fail("Images have different dimensions");
		}

		int width = expected.getWidth();
		int height = expected.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (expected.getRGB(x, y) != actual.getRGB(x, y)) {
					fail("Images are different, found a different pixel at: x = " + x + ", y = " + y);
				}
			}
		}
	}

	private void assertImagesEqual(BufferedImage expected, byte[] actualBytes) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(actualBytes);
		BufferedImage actual;
		try {
			actual = ImageIO.read(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		assertImagesEqual(expected, actual);
	}

	private BufferedImage loadImage(URL imageUrl) {
		try (InputStream openStream = imageUrl.openStream()) {
			return ImageIO.read(openStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private URL getImageUrl(String subdir, String fileName) {
		File file = new File(String.format("src/test/resources/%s/%s", subdir, fileName));

		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
