package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 全ユーザを取得する
    public List<Users> getallUsers() {
        return userRepository.findAll();
    }

    // ユーザーをIDで取得
    public Users getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
