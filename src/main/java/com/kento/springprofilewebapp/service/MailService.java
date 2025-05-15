package com.kento.springprofilewebapp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender; // メールを送信するために使用するメンバ変数

    // @Autowired
    // private RestTemplate restTemplate; // Webhookで送信する用のメンバ変数

    // @Value("${admin.webhook.url}") // application.properties
    // private String webhookUrl; // Webhook送付先のURL

    // メールは送付処理に時間が掛かるため、非同期処理とし、送信はバックグラウンドで行う。
    @Async
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@tomysan.net"); // 送付元のメールアドレス(省略可能、省略した場合はプロパティのメールアドレスになる)
        message.setTo(to); // 送付先
        message.setSubject(subject); // 件名
        message.setText(body); // 本文
        mailSender.send(message); // 送信実行
    }

    // Webhookを送信する
    // public void sendWebhook(String text) {
    //     try {
    //         String message = "テスト送信";
    //         Map<String, String> payload = new HashMap<>();
    //         payload.put("text", message);

    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);
    //         HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

    //         RestTemplate restTemplate = new RestTemplate();
    //         restTemplate.postForEntity(System.getenv("ADMIN_WEBHOOK_URL"), entity, String.class);
    //     } catch (Exception e) {
    //         System.out.println("送付失敗"); // 試験用
    //     }
    // }
}
