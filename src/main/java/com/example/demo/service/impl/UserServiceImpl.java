package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserEntity registerUser(UserEntity user) {
        return repo.save(user);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public UserEntity getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }
}