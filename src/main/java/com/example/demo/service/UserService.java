package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;

public interface UserService {

    User registerUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);
}




