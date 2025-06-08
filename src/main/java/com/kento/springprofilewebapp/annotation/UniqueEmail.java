package com.kento.springprofilewebapp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kento.springprofilewebapp.service.UniqueEmailValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD}) // 項目に対してバリデーションをかける場合はFIELDを選択する
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class) // バリデーションクラスを指定する
@Documented
public @interface UniqueEmail {
    String message() default "このメールアドレスはすでに登録されているメールアドレスです。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
