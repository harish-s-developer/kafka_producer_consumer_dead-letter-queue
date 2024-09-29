package com.kafka.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

		Message userProfile = null;
		boolean alreadyExists = false;
		try {
			userProfile = getUserByEmail(messageObj.getEmailId());
			alreadyExists = true;
			System.out.println("Email Id: " + messageObj.getEmailId() + " exists in the User Profile Directory");
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				System.out.println(
						"Email Id: " + messageObj.getEmailId() + " does not exist in the User Profile Directory");
			} else {
				System.out.println(exception.getMessage());
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		if (alreadyExists == true) {
			if ("U".equals(messageObj.getAction())) {
				updateUserById(messageObj);
				System.out.println("Updated User profile");
			} else if ("D".equals(messageObj.getAction())) {
				deleteUserById(messageObj.getUniqueId());
			} else {
				System.out.println("No updates required");
			}
		} else {
			if ("A".equals(messageObj.getAction()) || "U".equals(messageObj.getAction())) {
				addUser(messageObj);
				System.out.println("Added User profile");
			} else {
				System.out.println("No updates required");
			}
		}
	}

	public Message getUserByEmail(String email) {
		System.out.println("Getting user profile by email");
		String url = userProfileBaseUrl + "/api/users/email?email=" + email;
		ResponseEntity<Message> response = restTemplate.getForEntity(url, Message.class);
		return response.getBody();
	}

	public Message updateUserById(Message messageObj) {
		System.out.println("Updating User profile");
		String url = userProfileBaseUrl + "/api/users/" + messageObj.getUniqueId();
		Message updatedUserProfile = new Message();
		updatedUserProfile.setFirstName(messageObj.getFirstName());
		updatedUserProfile.setLastName(messageObj.getLastName());
		updatedUserProfile.setEmailId(messageObj.getEmailId());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Message> request = new HttpEntity<>(updatedUserProfile, headers);
		ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.PUT, request, Message.class);
		return response.getBody();
	}

	public Message addUser(Message messageObj) {
		System.out.println("Adding User profile");
		String url = userProfileBaseUrl + "/api/users";
		Message updatedUserProfile = new Message();
		updatedUserProfile.setFirstName(messageObj.getFirstName());
		updatedUserProfile.setLastName(messageObj.getLastName());
		updatedUserProfile.setEmailId(messageObj.getEmailId());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Message> request = new HttpEntity<>(updatedUserProfile, headers);
		ResponseEntity<Message> response = restTemplate.exchange(url, HttpMethod.POST, request, Message.class);
		return response.getBody();
	}

	public Void deleteUserById(Long uniqueId) {
		System.out.println("Deleting User profile");
		String url = userProfileBaseUrl + "/api/users/" + uniqueId;
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class,
				uniqueId);
		return response.getBody();
	}

}
