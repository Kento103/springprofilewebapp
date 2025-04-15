package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 最初の5件を取得する
    public List<Users> getLimitedUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).getContent(); // 最初のページの5件を取得する
    }

    // テーブルの全レコード件数を取得する
    public long countUsers() {
        return userRepository.count();
    }

    // 指定したユーザーを削除フラグを立てる(論理削除)
    @Transactional
    public void deletedUser(int id) {
        userRepository.softDelete(id);
    }

    // 指定したユーザーの削除フラグを取り消す(論理削除取り消し)
    @Transactional
    public void recoveryUser(int id) {
        userRepository.recoveryUser(id);
    }

    // 削除フラグ(論理削除)がついているユーザの一覧を表示する
    @Transactional
    public List<Users> deleted_list() {
        return userRepository.findByDeleted(); // 削除済みユーザを表示する
    }

    // 指定したユーザーを完全に削除する(物理削除)
    @Transactional
    public void removeUser(int id) {
        userRepository.removeUser(id);
    }
}
