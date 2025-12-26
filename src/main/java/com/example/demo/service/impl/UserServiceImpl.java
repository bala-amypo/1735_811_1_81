// File: src/main/java/com/example/demo/service/impl/UserServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {
        // Validate email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email already in use");
        }
        
        // Validate password length
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        
        // Validate department
        if (user.getDepartment() == null || user.getDepartment().trim().isEmpty()) {
            throw new ValidationException("Department is required");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
}