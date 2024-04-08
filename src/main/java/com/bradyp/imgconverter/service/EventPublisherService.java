package com.bradyp.imgconverter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bradyp.imgconverter.service.model.ImageConversionResult;

/**
 * Publishes the results of an image conversion job to a fictional queue, webhook or other event streaming service.
 */
@Service
public class EventPublisherService {
	private Logger logger = LoggerFactory.getLogger(EventPublisherService.class);

	public void publish(ImageConversionResult result) {
		if (result.isSuccess()) {
			logger.info("Pubishing successful conversion of {} from thread {}", result.getFileName(), Thread.currentThread().getName());
		} else {
			logger.info("Pubishing unsuccessful conversion of {} from thread {}", result.getFileName(), Thread.currentThread().getName());
		}
	}
}
