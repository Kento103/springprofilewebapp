package com.kento.springprofilewebapp.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
            .loginProcessingUrl("/login") // ログインページ
            .loginPage("/login") // ログインページの指定
            .defaultSuccessUrl("/login/ok", true) // ログイン成功した際にリダイレクトするページ
            .failureUrl("/login?error") // ログイン失敗したときに移動するページ
            .permitAll() // このページは誰でも許可
        ).logout(logout -> logout
            .logoutSuccessUrl("/") // ログアウト成功したときに遷移するページ
        ).authorizeHttpRequests(authz -> authz
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 静的なものはすべて権限なしでも許可する
            .requestMatchers("/user").hasAnyRole("USER", "ADMIN") // USERとADMINロールのみ許可する
            .requestMatchers("/admin/**").hasAnyRole("ADMIN") //管理者権限があるユーザーのみ閲覧を許可する
            .requestMatchers("/inquiry").hasAnyRole("ADMIN") // 管理者ユーザのみ許可する
            .requestMatchers("/register").hasAnyRole("ADMIN") // 管理者権限があるユーザのみ許可する
            .requestMatchers("/ranking").permitAll() // 権限なしでも許可する
            .requestMatchers("/inquiry/create").permitAll() // お問い合わせ画面
            .requestMatchers("/defaultimage/**").permitAll() // システム用画像は誰でも許可
            .requestMatchers("/users/*").permitAll() // ユーザページはだれでも見られる
            .requestMatchers("/users/*/like").permitAll() // ユーザページは誰でも見られる
            .requestMatchers("/users/*/liketop").permitAll() // ユーザページは誰でも見られる
            .requestMatchers("/users/*/likerank").permitAll() // ユーザページは誰でも見られる
            .requestMatchers("/").permitAll() // 権限なしでも許可する
            .anyRequest().authenticated() // ルールにないものは常に認証が必要
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
