package com.kento.springprofilewebapp.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterContoroller {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Users users, Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String hurigana, @RequestParam String description, @RequestParam int sexial, @RequestParam String role, @RequestParam int age, Model model, @Valid @ModelAttribute Users users, BindingResult bindingResult) {
        try {
            // パスワード専用バリデーションチェック(パスワードはハッシュ化するため、下の所でバリデーションチェック出来ない)
            // チェック用(正規表現)検査値
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,32}$");
            // 正規表現被検査値
            Matcher checkPassword = pattern.matcher(password);
            // 年齢欄にintの扱える最大値以上の値を挿入するとオーバーフローする。でかい値を入れる悪い子がいるため対策をする。
            // Math.incrementExact(age); // ageを検査する。でかい数字は例外を返す = catchへいく
            // パスワードが正規表現に一致しているかチェックする
            if (checkPassword.matches()) {
                // パスワード正規表現Pass
                // バリデーションチェック
                if (bindingResult.hasErrors()) {
                    // Getリクエスト用のメゾットを呼び出し、ユーザー登録画面に戻る
                    model.addAttribute("systemError", "入力が正しくありません\n確認してください！");
                    return showRegisterForm(users, model);
                }
            }
            else {
                // パスワード正規表現Fail
                model.addAttribute("passwordfail", "パスワードは半角英数と大文字、数字と_-のみ使えます。\n8～32文字以内で入力してください");
                bindingResult.hasErrors();
                model.addAttribute("systemError", "入力が正しくありません\n確認してください！");
                // Getリクエスト用のメゾットを呼び出し、ユーザー登録画面に戻る
                return showRegisterForm(users, model);
            }
            // 登録成功したときの処理
            userService.registerUser(username, email, password, hurigana, description, sexial, role, age); // @RequestParamから値を受け取る
            model.addAttribute("systemSuccess", "ユーザー登録が完了しました");
            return "login";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("systemError", "登録に失敗しました。入力内容を再確認してください！");
            return "register";
        }
    }

}
