package com.kento.springprofilewebapp.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import jakarta.validation.Valid;
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
        model.addAttribute("systemWarning", (String) model.getAttribute("systemWarning"));
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        model.addAttribute("systemError", (String) model.getAttribute("systemError"));
        return "user_settings";
    }

    // 一般ユーザ情報の変更(リダイレクト先要編集)
    @PostMapping("/setting")
    public String saveSetting(@AuthenticationPrincipal Users user, Model model, @RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) {
        model.addAttribute("user", user); // 情報をモデルに代入する
        if (email.isEmpty() && password.isEmpty()) {
            redirectAttributes.addFlashAttribute("systemWarning", "変更したい項目を入力してください");
            return "user_settings";
        }
        if (!email.isEmpty()) {
            Pattern emailPattern = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
            Matcher emailMatcher = emailPattern.matcher(email);
            if (!emailMatcher.matches()) {
                redirectAttributes.addFlashAttribute("systemError", "メールアドレスの形式が正しくありません");
                return "redirect:/setting";
            }
            user.setEmail(email);
        }
        if (!password.isEmpty()) {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,32}$");
            Matcher checkPassword = pattern.matcher(password);
            if (checkPassword.matches()) {
                user.setPassword(userService.passwordEncorded(password)); // パスワードをハッシュ化して、その値を保管する
                userService.save(user); // この内容を保存
            } else {
                redirectAttributes.addFlashAttribute("systemError", "パスワードは8～32文字かつ、半角英数字とハイフン、アンダーバーのみ使えます");
                return "redirect:/setting";
            }
        }
        redirectAttributes.addFlashAttribute("systemSuccess", "アカウント情報は変更されました");
        return "redirect:/";
    }

    // プロフィール編集画面(一般ユーザ)を表示する
    @GetMapping("/profile_edit")
    public String profileEdit(@AuthenticationPrincipal Users user, Model model) {
        model.addAttribute("user", user); // 情報をモデルに代入する。
        model.addAttribute("systemWarning", (String) model.getAttribute("systemWarning"));
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        model.addAttribute("systemError", (String) model.getAttribute("systemError"));
        return "profile_setting";
    }

    // プロフィールを更新する。
    @PostMapping("/profile_edit")
    public String saveProfile(
        @Valid @ModelAttribute("user") Users user,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("systemError", "入力内容を確認してください");
            return "profile_setting";
        }
        try {
            userService.updateProfile(user, user.getUsername(), user.getHurigana(), Integer.valueOf(user.getSexial()), user.getAge(), user.getDescription());
            redirectAttributes.addFlashAttribute("systemSuccess", "プロフィールの変更に成功しました");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("systemError", "保存に失敗しました！");
            return "redirect:/profile_edit";
        }
    }
}
