package com.kento.springprofilewebapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 各ユーザーページを表示する
    @GetMapping("/{id}")
    public String getUserPage(@PathVariable int id, Model model) {
        Users user = userService.getUserById(id); // usersテーブルのidから該当の情報尾を検索する
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        return "profile"; // profile.htmlのページを表示する
    }

    // ユーザー情報の編集ページを表示する
    @GetMapping("/{id}/edit")
    public String editUserPage(@PathVariable int id, Model model, Users user) {
        user = userService.getUserById(id); // usersテーブルのidから該当の情報を検索する
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        return "useredit"; //useredit.htmlのページを表示する
    }

    // ユーザー情報の更新を行うもの
    @PostMapping("/{id}")
    public String updateUser(
            @PathVariable int id,
            @ModelAttribute Users user,
            Model model,
            @ModelAttribute Users users,
            BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                //Getリクエスト用のメゾットを呼び出し、編集画面に戻る
                return editUserPage(user.getId(), model, user);
            }
            try {
                users = userService.updateUser(id, user);
                model.addAttribute("user", users);
                return "profile";
            } catch (Exception e) {
                model.addAttribute("error", "登録に失敗しました");
                return "useredit";
            }
        }
}
