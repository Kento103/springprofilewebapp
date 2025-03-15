package com.kento.springprofilewebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterContoroller {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        try {
            // 登録成功したときの処理
            userService.registerUser(username, email, password); // @RequestParamから値を受け取る
            model.addAttribute("success", "ユーザー登録が完了しました");
            return "login";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("error", "登録に失敗しました。入力内容を再確認してください！");
            return "register";
        }
    }

}
