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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("pageTitle", "新規ユーザー作成");
        return "register";
    }

    /**
     * 新規ユーザの作成を行います。バリデーション内容はModelを参照
     * @param username お名前
     * @param email メールアドレス
     * @param password パスワード(BCrypt)
     * @param hurigana ふりがな
     * @param description 自己紹介
     * @param sexial 性別
     * @param role 権限
     * @param age 年齢
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param users 代入不要
     * @param bindingResult バリデーション用代入不要
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return ユーザを登録します
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String hurigana, @RequestParam String description, @RequestParam int sexial, @RequestParam String role, @RequestParam int age, Model model, @Valid @ModelAttribute Users users, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
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
            redirectAttributes.addFlashAttribute("systemSuccess", "ユーザー登録が完了しました");
            return "redirect:/admin";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("systemError", "登録に失敗しました。入力内容を再確認してください！");
            return "register";
        }
    }

}
