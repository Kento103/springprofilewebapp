package com.kento.springprofilewebapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kento.springprofilewebapp.service.MailService;

@SpringBootTest
class SpringprofilewebappApplicationTests {
	public MailService mailService;
	@Test
	void contextLoads() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encode = bCryptPasswordEncoder.encode("12345");
		System.out.println("password:");
		System.out.println(encode);
	}

	// @Test
	// void sendWebhookTest() {
	// 	mailService.sendWebhook(null);
	// }

	@Test
	void sendEmailTest() {
		mailService.sendMail("kento103@outlook.com", "Junit送信テスト", "これは送信テストです。");
	}
}
