package com.kento.springprofilewebapp.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterContoroller {
    private final UserService userService;

    // @InitBinder
    // public void InitBinder(WebDataBinder binder) {
    //     // Integer型のフィールドに空文字が渡された場合はnullに変換する
    //     binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    // }

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
     * @param locked アクセス制御(0...アクセス許可,1...アクセス禁止)
     * @param file 画像ファイル(プロフィール画像)
     * @return ユーザを登録します
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String hurigana, @RequestParam String description, @RequestParam int sexial, @RequestParam String role, @RequestParam String locked, @RequestParam String age, @RequestParam("image") MultipartFile file, Model model, @Valid @ModelAttribute Users users, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userService.isExistEmail(email)) {
            redirectAttributes.addFlashAttribute("systemError", "このメールアドレスはすでに登録されています。");
            return "redirect:/register";
        }
        try {
            // パスワード専用バリデーションチェック(パスワードはハッシュ化するため、下の所でバリデーションチェック出来ない)
            // チェック用(正規表現)検査値
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,32}$");
            // 正規表現被検査値
            Matcher checkPassword = pattern.matcher(password);
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
            // 画像の追加処理及びバリデーション
            String uploadDir;
            String filename;
            Path path;
            String dbPass = null; // データベースに保管するためのパスを指定する
            if (!file.isEmpty()) {
                // ファイルのバリデーションチェックを行う
                userService.imageValidate(file);
                String uniqueFilename = UUID.randomUUID().toString(); // UUIDを生成し、ファイル名をランダムに作成する
                uploadDir = System.getProperty("user.dir") + "/uploads/"; // アップロードするディレクトリを指定する
                filename = "user_" + uniqueFilename + "_" + file.getOriginalFilename(); // ファイル名を指定する(file.getOriginalFilenameはファイル名をそのまま使用する為のもの)
                path = Paths.get(uploadDir + filename);
                Files.createDirectories(path.getParent()); // ディレクトリがない時は作成してくれる。
                file.transferTo(path.toFile());
                dbPass = "/images/" + filename; // データベースに保管する為のファイルの場所を格納する
            } else {
                System.out.println("画像検知なし");
            }


            // 登録成功したときの処理
            if (role.equals("ROLE_ADMIN")) {
                // 管理者の場合の処理
                userService.registerUser(username, email, password, "かんりしゃ", null, 0, role, "0", locked, null); // 不要なクエリに関してはダミーで登録する
            } else {
                // 一般ユーザの場合の処理
                userService.registerUser(username, email, password, hurigana, description, sexial, role, age, locked, dbPass); // @RequestParamから値を受け取る
            }
            redirectAttributes.addFlashAttribute("systemSuccess", "ユーザー登録が完了しました");
            return "redirect:/admin";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("systemError", "登録に失敗しました。入力内容を再確認してください！");
            return "register";
        }
    }

}
