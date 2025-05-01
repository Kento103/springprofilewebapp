package com.kento.springprofilewebapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kento.springprofilewebapp.model.Categorys;
import com.kento.springprofilewebapp.model.Inquirys;
import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.CategoryService;
import com.kento.springprofilewebapp.service.InquiryService;
import com.kento.springprofilewebapp.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final CategoryService categoryService;
    private final MailService mailService;

    // お問い合わせリスト
    @GetMapping
    public String inquiryTop(Model model, Model model2) {
        model.addAttribute("inquirys", inquiryService.getInquirysWithUser()); // userテーブル
        model2.addAttribute("categorys", inquiryService.getInquirysWithCategorys()); // categoryテーブル
        return "inquiry";
    }

    // お問い合わせ新規作成
    @GetMapping("/create")
    public String inquieyCreate(Model model) {
        List<Categorys> categorys = categoryService.getAllLists(); // カテゴリをDBから全件取得する
        model.addAttribute("categorys", categorys); // 取得したカテゴリリストをcategorysに入れる
        return "inquiry_create";
    }

    // お問い合わせ内容を送信する(Postリクエスト)
    @PostMapping("/create")
    public String inquieyAdd(@RequestParam String description, @RequestParam int category, @AuthenticationPrincipal Users loginUser, Model model) {
        try {
            // 登録成功したときの処理
            inquiryService.registeInquiry(description, category, loginUser);
            model.addAttribute("success", "お問い合わせの追加に成功しました");
            // 本文を設定する
            String mailBody = "新規のお問い合わせがありました。お問い合わせ内容は以下の通りです。\n\n" + description + "\n\nお問い合わせ管理のページを開いて、対応してください。";
            // メールの送信先
            String mailTo = System.getenv("SPRING_MAIL_USERNAME");
            // メールを送信する
            mailService.sendMail(mailTo, "新規のお問い合わせがありました", mailBody);
            return "redirect:/";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("error", "お問い合わせの追加に失敗しました！内容を確認してください");
            return "inquiry_create";
        }
    }

    // 各お問い合わせ内容を表示する
    @GetMapping("/{id}")
    public String getInquiry(@PathVariable int id, Model model) {
        Inquirys inquirys = inquiryService.getInquirysById(id);
        model.addAttribute("inquiry", inquirys);
        return "inquiry_description";
    }

    // お問い合わせのステータスを更新する(Postリクエスト)
    @PostMapping("/{id}/update")
    public String updateInquiry(@PathVariable int id, @RequestParam int status) {
        System.out.println(id);
        System.out.println(status);
        Inquirys inquiry = inquiryService.getInquirysById(id);
        inquiry.setStatus(status);
        inquiryService.updateInquiry(inquiry);
        return "redirect:/inquiry";
    }

    // カテゴリーリストを表示する
    @GetMapping("/category")
    public String categoryList(Model model) {
        model.addAttribute("categorys", categoryService.getAllLists());
        return "category_list";
    }

    // カテゴリーの追加(Getリクエスト)
    @GetMapping("/category/create")
    public String createCategory(Categorys categorys, Model model) {
        model.addAttribute("categorys", categorys);
        return "category_create";
    }

    // カテゴリの追加処理(Postリクエスト)
    @PostMapping("/category/create")
    public String registerCategory(@RequestParam String name, @ModelAttribute Categorys categorys, Model model) {
        try {
            // 登録成功した時の処理
            categoryService.registeCategorys(name);
            model.addAttribute("success", "カテゴリ登録に成功しました");
            return "redirect:/inquiry/category";
        } catch (Exception e) {
            model.addAttribute("error", "登録に失敗しました。入力内容を確認してください！");
            return "category_create";
        }
    }

    // カテゴリを削除する(Postリクエスト)
    @PostMapping("/category/{id}/delete")
    public String deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return "redirect:/inquiry/category";
    }

    // カテゴリを編集する
    @GetMapping("/category/{id}/edit")
    public String editCategory(@PathVariable int id, Categorys categorys, Model model) {
        categorys = categoryService.getCategorysById(id);
        model.addAttribute("categorys", categorys);
        return "category_edit";
    }

    // 編集結果を保存する(Postリクエスト)
    @PostMapping("/category/{id}/edit")
    public String saveEditCategory(@PathVariable int id, @ModelAttribute Categorys categorys, @ModelAttribute Categorys category , Model model) {
        try {
            categorys =categoryService.updateCategorys(id, category);
            model.addAttribute("category", category);
            return "redirect:/inquiry/category";
        } catch (Exception e) {
            model.addAttribute("error" ,"登録に失敗しました。入力内容を確認してください");
            return "category_edit";
        }
    }

    // @GetMapping("/test")
    // public String inquiryTes(Model model) {
    //     model.addAttribute("inquirys", inquiryService.getInquirysWithUser());
    //     return "inquiry";
    // }
}
