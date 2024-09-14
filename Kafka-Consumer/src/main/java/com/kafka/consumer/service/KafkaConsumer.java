package com.kafka.consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.model.Message;

@Service
public class KafkaConsumer {

	@KafkaListener(topics = "${spring.kafka.consumer.topic}")
	public void consume(String message) {
		System.out.println("Consumed message: " + message);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Message messageObj = objectMapper.readValue(message, Message.class);
			System.out.println("Action: " + messageObj.getAction());
			System.out.println("Unique Id: " + messageObj.getUniqueId());
			System.out.println("First Name: " + messageObj.getFirstName());
			System.out.println("Last Name: " + messageObj.getLastName());
			System.out.println("Email Id: " + messageObj.getEmailId());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}
}
