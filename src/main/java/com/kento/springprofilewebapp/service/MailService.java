package com.kento.springprofilewebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender; // メールを送信するために使用するメンバ変数

    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@tomysan.net"); // 送付元のメールアドレス(省略可能、省略した場合はプロパティのメールアドレスになる)
        message.setTo(to); // 送付先
        message.setSubject(subject); // 件名
        message.setText(body); // 本文
        mailSender.send(message); // 送信実行
    }
}
