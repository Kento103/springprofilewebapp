package com.kento.springprofilewebapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;
import com.kento.springprofilewebapp.controller.UserController;

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

    @GetMapping("/login/ok")
    public String loginSuccess(@AuthenticationPrincipal Users user) {
        user.getRole();
        System.out.println(user.getRole()); // 試験
        if (user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/admin"; // 管理者用ダッシュボードに移動する。
        }
        // 管理者以外は月間ランキングに移動する(プロフ画面)
        return "redirect:/ranking";
    }
}
