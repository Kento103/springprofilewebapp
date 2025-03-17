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

    // ユーザー登録する
    public Users registerUser(String username, String email, String password) {
        String encordedPassword = passwordEncoder.encode(password); // パスワードをハッシュ化する
        Users user = new Users(username, email, encordedPassword, "ROLE_USER"); // パスワードはハッシュ化して、ロールはユーザーで保管する
        return userRepository.save(user); // DBにユーザー情報を保管する
    }
}
