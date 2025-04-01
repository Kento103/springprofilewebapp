package com.kento.springprofilewebapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            Model model) { // 取得したものを入れる用のもの
        model.addAttribute("users", userService.getLimitedUsers(page, 5)); // page:ページ数 size：いくつ取得するか
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
}
