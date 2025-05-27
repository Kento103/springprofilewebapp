package com.kento.springprofilewebapp.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class TopController {
    private final UserService userService;

    // いいねの多い順に並び替える
    @GetMapping("/ranking")
    public String getLikeRanking(Model model) {
        model.addAttribute("users", userService.getMostLikeUsers()); // 月間いいねの多い順に並び変えた状態でユーザリストを取得する
        model.addAttribute("usersYear", userService.getMostYearsLikeUsers()); // 年間いいねの多い順に並び替える
        return "ranking";
    }

    // 一般ユーザアカウント設定用
    @GetMapping("/setting")
    public String userSetting(@AuthenticationPrincipal Users user, Model model) {
        model.addAttribute("user", user); // 情報をモデルに代入する
        return "user_settings";
    }

    // 一般ユーザ情報の変更(リダイレクト先要編集)
    @PostMapping("/setting")
    public String saveSetting(@AuthenticationPrincipal Users user, Model model, @RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) {
        model.addAttribute("user", user); // 情報をモデルに代入する
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        if (!password.isEmpty()) {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,32}$");
            Matcher checkPassword = pattern.matcher(password);
            if (checkPassword.matches()) {
                user.setPassword(userService.passwordEncorded(password)); // パスワードをハッシュ化して、その値を保管する
                userService.save(user); // この内容を保存
                System.out.println("保存OK");
                return "redirect:/";
            }
            System.out.println("保存NG");
        }
        return "user_settings";
    }
}
