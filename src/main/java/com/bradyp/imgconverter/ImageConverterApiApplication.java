package com.bradyp.imgconverter;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Main entry class for the Image Converter application.
 */
@SpringBootApplication
@EnableAsync
public class ImageConverterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageConverterApiApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("ImageConverter-");
		executor.initialize();
		return executor;
	}
}
