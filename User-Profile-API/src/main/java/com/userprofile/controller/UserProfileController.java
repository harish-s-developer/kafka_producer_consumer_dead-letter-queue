package com.userprofile.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userprofile.entity.UserProfile;
import com.userprofile.repository.UserProfileRepository;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

	@Autowired
	private UserProfileRepository userProfileRepository;

	@GetMapping
	public List<UserProfile> getAllUsers() {
		return userProfileRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserProfile> getUserById(@PathVariable Long id) {
		Optional<UserProfile> userProfile = userProfileRepository.findById(id);
		return userProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/email")
	public ResponseEntity<UserProfile> getUserByEmail(@RequestParam String email) {
		Optional<UserProfile> userProfile = userProfileRepository.findByEmailId(email);
		return userProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public UserProfile createUser(@RequestBody UserProfile userProfile) {
		return userProfileRepository.save(userProfile);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserProfile> updateUser(@PathVariable Long id, @RequestBody UserProfile userProfile) {
		Optional<UserProfile> existingUserProfile = userProfileRepository.findById(id);
		if (existingUserProfile.isPresent()) {
			UserProfile updatedUserProfile = existingUserProfile.get();
			updatedUserProfile.setFirstName(userProfile.getFirstName());
			updatedUserProfile.setLastName(userProfile.getLastName());
			updatedUserProfile.setEmailId(userProfile.getEmailId());

			return ResponseEntity.ok(userProfileRepository.save(updatedUserProfile));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userProfileRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
