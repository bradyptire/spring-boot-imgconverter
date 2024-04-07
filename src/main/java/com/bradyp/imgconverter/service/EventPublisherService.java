package com.bradyp.imgconverter.service;

import org.springframework.stereotype.Service;

import com.bradyp.imgconverter.service.model.ImageConversionResult;

/**
 * Publishes the results of an image conversion job to a fictional queue, webhook or other event streaming service.
 */
@Service
public class EventPublisherService {
	public void publish(ImageConversionResult result) {
		if (result.isSuccess()) {
			System.out.println(String.format("Pubishing successful conversion of %s from thread %s", result.getFileName(), Thread.currentThread().getName()));
		} else {
			System.out.println(String.format("Pubishing successful conversion of %s from thread %s", result.getFileName(), Thread.currentThread().getName()));
		}
	}
}
