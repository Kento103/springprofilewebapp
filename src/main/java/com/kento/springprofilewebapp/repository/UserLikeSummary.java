package com.kento.springprofilewebapp.repository;

public interface UserLikeSummary {
    Integer getUserId();
    String getUsername();
    Long getTotalLikes();
    String getImagePath();
    String getDescription();
}
