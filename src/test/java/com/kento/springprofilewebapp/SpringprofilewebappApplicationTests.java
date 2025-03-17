package com.kento.springprofilewebapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class SpringprofilewebappApplicationTests {

	@Test
	void contextLoads() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encode = bCryptPasswordEncoder.encode("12345");
		System.out.println("password:");
		System.out.println(encode);
	}

}
