package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService ser;

    public UserController(UserService ser) {
        this.ser = ser;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return ser.registerUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return ser.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return ser.getUserById(id);
    }
}