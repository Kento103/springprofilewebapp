package com.kento.springprofilewebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String inquiryTop(Model model, Model model2) {
        model.addAttribute("inquirys", inquiryService.getInquirysWithUser()); // userテーブル
        model2.addAttribute("categorys", inquiryService.getInquirysWithCategorys()); // categoryテーブル
        return "inquiry";
    }

    // お問い合わせ新規作成
    @GetMapping("/create")
    public String inquieyCreate() {
        return "inquiry_create";
    }

    // 各お問い合わせ内容を表示する
    @GetMapping("/{id}")
    public String getInquiry(@PathVariable int id, Model model) {
        Inquirys inquirys = inquiryService.getInquirysById(id);
        model.addAttribute("inquiry", inquirys);
        return "inquiry_description";
    }

    // @GetMapping("/test")
    // public String inquiryTes(Model model) {
    //     model.addAttribute("inquirys", inquiryService.getInquirysWithUser());
    //     return "inquiry";
    // }
}
