// package com.kento.springprofilewebapp.controller;

// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;

// import com.kento.springprofilewebapp.service.MailService;

// import lombok.RequiredArgsConstructor;

// @ControllerAdvice
// @RequiredArgsConstructor
// public class GlobalExceptionHandler {
//     private final MailService mailService;

//     // 500エラー
//     @ExceptionHandler(Exception.class)
//     public String handleException(Exception ex, Model model) {
//         try {
//             System.out.println("500エラー検知");
//             // ログを出力する
//             ex.printStackTrace();
//             // メールタイトル用
//             String mailTitle = "【500エラー発生】Internal Server Error";
//             // メール本文用
//             String mailBody = "予期せぬエラーが発生しました。エラー内容は以下の通りです\nエラー内容：\n" + ex.toString() + "\n\n\n詳細なエラー内容：\n" + ex.getLocalizedMessage() + "\n\n\nエラー原因：\n" + ex.getCause() + "\n\n\n詳細ログ：\n" + java.util.Arrays.toString(ex.getStackTrace());
//             // 管理者にメールを送信する
//             mailService.sendMail(System.getenv("SPRING_MAIL_USERNAME"), mailTitle, mailBody);

//             return "error/500";
//         } catch (Exception e) {
//             // メール送付に失敗してもログは出力する
//             e.printStackTrace();
//             System.out.println("500エラー検知+メール送付失敗");
//             return "error/500";
//         }
        
//     }
// }
