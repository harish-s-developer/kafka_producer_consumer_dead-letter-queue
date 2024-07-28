package com.kafka.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kafka.producer.service.KafkaProducerService;

@Controller
public class KafkaProducerController {

	@Autowired
	private KafkaProducerService kafkaProducerService;

	@PostMapping("/send")
	public ResponseEntity<String> sendMessage(@RequestBody String message) {
		try {
			kafkaProducerService.sendMessage("test", message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return ResponseEntity.ok("Message pushed to topic !");
	}
}