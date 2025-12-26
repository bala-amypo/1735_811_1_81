package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.ValidationException;

import java.util.List;

public interface UserService {

    User registerUser(User user) throws ValidationException;

    User getUser(Long id);

    List<User> getAllUsers();
}
