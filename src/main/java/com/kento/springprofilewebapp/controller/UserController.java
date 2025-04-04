package com.kento.springprofilewebapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller // これは！コントローラ！！！
@RequestMapping("/users")  // マッピング！
@RequiredArgsConstructor // コンストラクタを自動で入れてくれる物！
public class UserController {
    private final UserService userService; // 定数にしておくよ！

    // 各ユーザーページを表示する
    @GetMapping("/{id}")
    public String getUserPage(@PathVariable int id, Model model) {
        Users user = userService.getUserById(id); // usersテーブルのidから該当の情報尾を検索する
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        return "profile"; // profile.htmlのページを表示する
    }

    // ユーザー情報の編集ページを表示する
    @GetMapping("/{id}/edit")
    public String editUserPage(@PathVariable int id, Model model, Users user, @AuthenticationPrincipal Users loginUser) {
        user = userService.getUserById(id); // usersテーブルのidから該当の情報を検索する
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        if (loginUser.getRole().equals("ROLE_ADMIN")) { // 自分がROLE_ADMIN保有者か確認するloginUserはオブジェクトなので#.equalsでないと不一致となる(==ではだめ)
            // 管理者権限ロールでログイン中の場合はどのユーザの編集もできる
            return "useredit"; // useredit.htmlのページを表示する
        } else {
            // それ以外のユーザは自分のみの編集ができる
            if (user.getId() == loginUser.getId()) { // user.getId()...閲覧中のUserID loginUser.getId()...ログイン中のユーザーID
                return "useredit"; // useredit.htmlのページを表示する
            } else {
                model.addAttribute("error", "権限がありません");
                return "redirect:/users/{id}"; // プロフィールに戻す
            }
        }
    }

    // ユーザー情報の更新を行うもの
    @PostMapping("/{id}")
    public String updateUser(
            @PathVariable int id,
            @ModelAttribute Users user,
            Model model,
            @ModelAttribute Users users,
            @AuthenticationPrincipal Users loginUsers,
            BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                //Getリクエスト用のメゾットを呼び出し、編集画面に戻る
                return editUserPage(user.getId(), model, user, loginUsers);
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
    
    // ユーザーリスト一覧(ここは、usersのルートで表示したいので、@GetMappingの)引数は入れない！
    // そのうち抹消対象
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.getallUsers());
        return "userlist";
    }

    // ユーザーを指定したページから5件だけ取得する
    @GetMapping("/list")
    public String getUsers(
            @RequestParam(defaultValue = "0") int page, // URL?page=0でページを取得できる,defaultValueで指定しなかったときの初期値を指定できる。
            Model model) { // 取得したものを入れる用のもの
        model.addAttribute("users", userService.getLimitedUsers(page, 5)); // page:ページ数 size：いくつ取得するか
        return "userlist";
    }
}
