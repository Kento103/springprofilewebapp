package com.kento.springprofilewebapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
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
    public String profileEdit(@AuthenticationPrincipal Users loginuser, Users user, Model model) {
        user = userService.getUserById(loginuser.getId()); // 現在のログイン中のユーザで検索
        model.addAttribute("user", user); // 情報をモデルに代入する。
        model.addAttribute("systemWarning", (String) model.getAttribute("systemWarning"));
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        model.addAttribute("systemError", (String) model.getAttribute("systemError"));
        return "profile_setting";
    }

    // プロフィールを更新する。
    @PostMapping("/profile_edit")
    public String saveProfile(
        @Valid @ModelAttribute("user") Users user, // 値の受け取りをするためのもの(userで受け取らないと、バリデーションが効かない)
        BindingResult bindingResult, // バリデーション
        @AuthenticationPrincipal Users loginUser, // ログイン中のユーザ情報
        @RequestParam(value = "image", required = false) MultipartFile file, // 画像ファイルの処理
        RedirectAttributes redirectAttributes, // リダイレクト制御
        Model model // モデル
    ) {
        // 値を上書きする。こうすることでhtml側で不正に書き換えられても絶対に書き換えさせない。
        // htmlに書いている理由として、@validが最初の段階で検査されてしまい、値が空と怒られてしまう。そのためhtmlにやむを得ず記載している
        user.setId(loginUser.getId());
        user.setEmail(loginUser.getEmail());
        user.setPassword(loginUser.getPassword());
        user.setRole(loginUser.getRole());
        user.setLocked(loginUser.isLocked());
        user.setDeleted(loginUser.isDeleted());
        Users dbUser = userService.getUserById(loginUser.getId());
        if (!file.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/"; // アップロードするディレクトリを指定する
                // ファイルのバリデーションチェックを行う
                userService.imageValidate(file);
                // 画像が設定されていた場合は、以前設定されていた画像ファイルをサーバから削除する
                String imagePath = dbUser.getImagePath();
                System.out.println("imagePath=[" + imagePath + "]");
                if (imagePath != null && !imagePath.isEmpty()) {
                    System.out.println(dbUser.getImagePath()); // 試験用
                    System.out.println(dbUser.getImagePath().substring(8)); // 先頭から8文字を削除する(ファイルパスが/images/のため)
                    String deleteImageFilename = dbUser.getImagePath().substring(8); // 先頭の/images/の文字列を削除する
                    Path deletePath = Paths.get(uploadDir + deleteImageFilename); // 削除する画像のパスを指定する
                    Files.delete(deletePath); // 変更前に設定されていた画像を削除する
                }
                String filename = "user_" + loginUser.getId() + "_" + file.getOriginalFilename(); // ファイル名を指定する(file.getOriginalFilenameはファイル名をそのまま使用する為のもの)
                Path path = Paths.get(uploadDir + filename);
                Files.createDirectories(path.getParent()); // ディレクトリがない時は作成してくれる。
                file.transferTo(path.toFile());

                // データベースにデータを保管する
                user.setImagePath("/images/" + filename); // Webでアクセスするパスになる
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("systemError", "画像ファイルをアップロードしてください");
                return "redirect:/users/{id}/edit";
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("systemError", "予期せぬエラーが発生しました");
                return "redirect:/users/{id}/edit";
            } catch (MaxUploadSizeExceededException e) {
                redirectAttributes.addFlashAttribute("systemError", "画像のファイルサイズは最大2MBまでです");
                return "redirect:/users/{id}/edit";
            }
        } else {
            user.setImagePath(dbUser.getImagePath());
        }
        // バリデーション検査
        if (bindingResult.hasErrors()) {
            // 警告画面を呼び出し、ユーザ登録画面に戻る
            model.addAttribute("systemError", "入力が正しくありません\n確認してください！");
            return "profile_setting";
        }
        try {
            // 結果をDBに保存する
            userService.save(user);
            redirectAttributes.addFlashAttribute("systemSuccess", "プロフィール情報の変更に成功しました");
        } catch (Exception e) {
            model.addAttribute("systemError", "保存に失敗しました！");
        }
        return "redirect:/profile_edit";
    }
}
