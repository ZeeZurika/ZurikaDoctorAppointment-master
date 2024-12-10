package org.zurika.zeehealth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zurika.zeehealth.model.*;
import org.zurika.zeehealth.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users sorted by id in descending order
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // Get all doctors
    public List<User> getAllDoctors() {
        return userRepository.findByRole(UserRole.DOCTOR);
    }

    // Get a user by ID
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Get a user by username
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Admin - Add a new user
    public User addUser(String username, String email, String password, String firstName, String lastName, String role) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Using a PasswordEncoder to hash the password
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setRole(UserRole.valueOf(role.toUpperCase()));
        return userRepository.save(newUser);
    }

    // Admin - Delete a user
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Update personal information for a patient
    public void updatePatientInfo(Long userId, String firstName, String lastName, String email, String password) {
        User user = getUserById(userId);

        // Update fields
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        // Only update the password if a new one is provided
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(user);
    }
}
