package com.kento.springprofilewebapp.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.kento.springprofilewebapp.annotation.UniqueEmail;
import com.kento.springprofilewebapp.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return !userRepository.existsByEmail(email);
    }

}
