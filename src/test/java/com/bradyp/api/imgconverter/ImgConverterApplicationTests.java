package com.bradyp.api.imgconverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bradyp.imgconverter.ImageConverterApiApplication;
import com.bradyp.imgconverter.api.v1.model.ImageConversionRequest;
import com.bradyp.imgconverter.api.v1.model.ImageConversionResponse;
import com.bradyp.imgconverter.api.v1.model.UnsupportedFormatExceptionResponse;
import com.bradyp.imgconverter.service.EventPublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ImageConverterApiApplication.class)
@AutoConfigureMockMvc
class ImgConverterApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EventPublisherService employeeRepository;

	@DisplayName("Test get supported formats")
	@Test
	public void whenGetSupportedFormatsThenExpectedSupportedFormatsReturned() throws Exception {
		mvc.perform(get("/api/v1/images/conversions/formats").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0]", is("jpg")));
	}

	@DisplayName("Test /convert happy path")
	@Test
	public void whenConvertThenSuccess() throws Exception {
		File imageFile = new File(String.format("src/test/resources/for-jpg-tests/jpg-test.jpg"));

		ImageConversionRequest request = new ImageConversionRequest();
		URL imageFileUrl = imageFile.toURI().toURL();
		request.setSourceImage(imageFileUrl);
		request.setToFormat("png");

		// Post to conversion endpoint
		MvcResult postResult = mvc
				.perform(post("/api/v1/images/conversions/convert").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.format", is("png")))
				.andExpect(jsonPath("$.path", matchesRegex("\\/api\\/v1\\/images\\/conversions\\/.*.png"))).andReturn();

		ImageConversionResponse response = objectMapper.readValue(postResult.getResponse().getContentAsByteArray(),
				ImageConversionResponse.class);

		// Retrieve converted image
		MvcResult getResult = mvc.perform(
				get(String.format("http://localhost/%s", response.path())).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		byte[] imageBytes = getResult.getResponse().getContentAsByteArray();

		// Parse the response as an image and call that success.
		ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
		ImageIO.read(inputStream);
	}

	@DisplayName("Test /convert unsupported format returns 400")
	@Test
	public void whenConvertUnsupportedFormatThen400Response() throws Exception {
		File imageFile = new File(String.format("src/test/resources/for-jpg-tests/jpg-test.jpg"));

		ImageConversionRequest request = new ImageConversionRequest();
		URL imageFileUrl = imageFile.toURI().toURL();
		request.setSourceImage(imageFileUrl);
		request.setToFormat("svg");

		mvc.perform(post("/api/v1/images/conversions/convert").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
				.andExpect(result -> assertThat(
						result.getResolvedException() instanceof UnsupportedFormatExceptionResponse));

	}

	@DisplayName("Test /convert/async happy path")
	@Test
	public void whenConvertAsyncThenSuccess() throws Exception {
		File imageFile = new File(String.format("src/test/resources/for-jpg-tests/jpg-test.jpg"));

		ImageConversionRequest request = new ImageConversionRequest();
		URL imageFileUrl = imageFile.toURI().toURL();
		request.setSourceImage(imageFileUrl);
		request.setToFormat("png");

		MvcResult postResult = mvc
				.perform(post("/api/v1/images/conversions/convert/async").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isAccepted())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.format", is("png")))
				.andExpect(jsonPath("$.path", matchesRegex("\\/api\\/v1\\/images\\/conversions\\/.*.png"))).andReturn();
	}
}
