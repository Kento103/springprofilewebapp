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
    public Users registerUser(String username, String email, String password, String hurigana, String description, int sexial, String role, int age) {
        String encordedPassword = passwordEncoder.encode(password); // パスワードをハッシュ化する
        Users user = new Users(username, email, encordedPassword, role, hurigana, description, sexial, age); // パスワードはハッシュ化して、ロールはユーザーで保管する
        return userRepository.save(user); // DBにユーザー情報を保管する
    }

    // ユーザー情報を編集する
    public Users updateUser(int id, Users updatedUser) {
        return userRepository.findById(id) // ユーザーをIDで検索する
            .map(user -> { // #.mapでOptionalないのデータを取り出し、編集している
                user.setUsername(updatedUser.getUsername()); // ユーザーネームを取得して変数に代入する
                user.setHurigana(updatedUser.getHurigana()); // ふりがな
                user.setEmail(updatedUser.getEmail()); // 登録メールアドレス
                user.setDescription(updatedUser.getDescription()); // 自己紹介
                return userRepository.save(user); // #.saveでデータベースの情報を更新する
            })
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません")); // 該当するユーザーが三つからない場合はこのエラーに遷移する
    }
}
