package com.batch57.gdipsa.group6.lapsbackend.model.login;

public class LoginRequest {
    Integer userId;
    String password;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
