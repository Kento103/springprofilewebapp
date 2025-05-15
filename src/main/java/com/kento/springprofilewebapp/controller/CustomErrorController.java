package com.kento.springprofilewebapp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController{

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        if (statusCode == 404) {
            System.out.println("404エラー検知");
            return "error/404";
        } else if (statusCode == 403) {
            System.out.println("403エラー検知");
            return "error/403";
        }
        return "error/500";
    }
}
