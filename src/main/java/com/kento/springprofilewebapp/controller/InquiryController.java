package com.kento.springprofilewebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kento.springprofilewebapp.model.Inquirys;
import com.kento.springprofilewebapp.service.InquiryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    // お問い合わせリスト
    @GetMapping
    public String inquiryTop(Model model) {
        model.addAttribute("inquirys", inquiryService.getAllLists());
        return "inquiry";
    }

    // お問い合わせ新規作成
    @GetMapping("/create")
    public String inquieyCreate() {
        return "inquiry_create";
    }
}
