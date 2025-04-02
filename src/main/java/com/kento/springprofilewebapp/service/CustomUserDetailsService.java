package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// ビジネスロジック！！！
@Service
@RequiredArgsConstructor // コンストラクタの自動設定
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + "が見つかりません！"));
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll(); // ユーザーレポジトリから全件取得する
    }
}
