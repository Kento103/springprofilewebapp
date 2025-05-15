package com.kento.springprofilewebapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login"; // ログインページの表示をする。
    }

    @GetMapping("/")
    public String top(Model model) {
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess")); // リダイレクトもとからのデータを受け取る
        List<Users> users = userService.getallUsers(); // 全ユーザを取得する
        model.addAttribute("users", users); // ユーザーリストをすべてリストに格納する
        return "top"; // トップページの表示をする。
    }

    @GetMapping("/dashboard")
    public String user() {
        return "user"; // ユーザーページの表示
    }
}
