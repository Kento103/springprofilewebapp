package com.kento.springprofilewebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // 非同期処理を有効化する為に必要な設定このアノテーションは必ずつける事。
public class SpringprofilewebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringprofilewebappApplication.class, args);
	}

}
