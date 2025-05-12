package com.kento.springprofilewebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("users", userService.getMostLikeUsers()); // いいねの多い順に並び変えた状態でユーザリストを取得する
        return "ranking";
    }
}
