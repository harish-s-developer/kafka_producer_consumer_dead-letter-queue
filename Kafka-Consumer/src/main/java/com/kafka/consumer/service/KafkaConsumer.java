package com.kafka.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.model.Message;

@Service
public class KafkaConsumer {

	@Autowired
	RestTemplate restTemplate;

	@Value("${user_profile_base_url}")
	String userProfileBaseUrl;

	@KafkaListener(topics = "${spring.kafka.consumer.topic}")
	public void consume(String message) {

		System.out.println("Consumed message: " + message);

		ObjectMapper objectMapper = new ObjectMapper();
		Message messageObj = null;
		try {
			messageObj = objectMapper.readValue(message, Message.class);
			System.out.println("Action: " + messageObj.getAction());
			System.out.println("Unique Id: " + messageObj.getUniqueId());
			System.out.println("First Name: " + messageObj.getFirstName());
			System.out.println("Last Name: " + messageObj.getLastName());
			System.out.println("Email Id: " + messageObj.getEmailId());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		try {
			System.out.println("Hello");
			Message userProfile = getUserByEmail(messageObj.getEmailId());
			System.out.println("Email Id: " + userProfile.getEmailId());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	public Message getUserByEmail(String email) {
		String url = userProfileBaseUrl + "/email?email=" + email;
		ResponseEntity<Message> response = restTemplate.getForEntity(url, Message.class);
		return response.getBody();
	}
}
