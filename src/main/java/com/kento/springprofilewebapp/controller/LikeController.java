package com.kento.springprofilewebapp.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.LikeService;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/addlike/{id}")
    @ResponseBody
    public CompletableFuture<Integer> addLikeUser(@PathVariable Integer id) {
        return likeService.addLikeAsync(null, id);
    }
}
