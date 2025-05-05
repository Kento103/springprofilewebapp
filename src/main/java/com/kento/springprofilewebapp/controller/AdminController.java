package com.kento.springprofilewebapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller // コントローラ
@RequestMapping("/admin") // マッピング
@RequiredArgsConstructor // コンストラクタを自動で入れてくれるもの
public class AdminController {
    private final UserService userService;
    // 管理者ページトップ
    @GetMapping
    public String admintop() {
        return "admin";
    }

    // 現在登録されているユーザーリストを表示する
    @GetMapping("/list")
    public String getUsers(
            @RequestParam(defaultValue = "0") int page, // URL?page=0でページを取得できる,defaultValueで指定しなかったときの初期値を指定できる。
            Model allUser, // ユーザー数を検索する(前ページ、次ページを出すために使うもの)
            Model model) { // 取得したものを入れる用のもの
        long maxPage = userService.countUsers() / 5;
        model.addAttribute("users", userService.getLimitedUsers(page, 5)); // page:ページ数 size：いくつ取得するか
        allUser.addAttribute("allUsers", userService.countUsers()); // 全ユーザー人数確認用
        allUser.addAttribute("page", page); // 現在のページ
        allUser.addAttribute("maxpage", maxPage); // 最大ページ
        return "userlist";
    }

    // 削除されているユーザーリストを表示する
    @GetMapping("/deleted_list")
    public String delUsers(Model model) { // 取得したものを入れるようの物
        model.addAttribute("users", userService.deleted_list());
        return "deleted_list";
    }

    // ユーザーを削除する(Postリクエスト)
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id) {
        userService.deletedUser(id);
        return "redirect:/admin/list";
    }

    // ユーザーを復元する(Postリクエスト)
    @PostMapping("/{id}/recovery")
    public String recoveryUser(@PathVariable int id) {
        userService.recoveryUser(id);
        return "redirect:/admin/deleted_list";
    }

    // ユーザーを完全消去(物理削除)する(Postリクエスト)
    @PostMapping("/{id}/delete_all")
    public String deleteAllUser(@PathVariable int id) {
        userService.removeUser(id); // 完全削除コマンド。実行注意。
        return "redirect:/admin/deleted_list";
    }

    // ユーザのアクセス権限を変更する
    @PostMapping("/{id}/grant")
    public String changeGrant(@PathVariable int id, Users user, Model model) {
        user = userService.getUserById(id); // 該当のユーザを検索する
        if (user.getRole().equals("ROLE_USER")) {
            // 現在の権限がUSERの場合はADMINに変更する
            userService.changeGrant(id, "ROLE_ADMIN");
        } else if (user.getRole().equals("ROLE_ADMIN")) {
            // 現在の権限がADMINの場合はUSERに変更する
            userService.changeGrant(id, "ROLE_USER");
        } else {
            // どれにも一致しない場合はなにもしない
        }
        return "redirect:/admin/list";
    }

    // ユーザのアカウントロック状態を変更する
    @PostMapping("{id}/locked")
    public String changeLock(@PathVariable int id, Users user, Model model) {
        user = userService.getUserById(id);
        System.out.println(user.isAccountNonLocked());
        if (user.isAccountNonLocked()) {
            // ロックされていないときはロックする
            userService.changeLock(id);
        } else if (!user.isAccountNonLocked()) {
            // ロックされている時はロック解除する
            userService.changeUnLock(id);
        }
        return "redirect:/admin/list";
    }
}
