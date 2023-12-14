package com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.USER_TYPE;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "Joined" 继承策略是一种将继承关系映射到数据库表的方法，其中每个子类都映射到自己的数据库表，同时父类也映射到一个单独的数据库表
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("user_id") //
    private Integer user_id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min=3,max=20, message = "username must be 3-20 characters")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min=6,max=10, message = "password must be 6-10 characters")
    private String password;

    @Column(nullable = false)
    private USER_TYPE userType;

    public User() {
    }

    public User(String name, String password, USER_TYPE userType) {
        this.name = name;
        this.password = password;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public USER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(USER_TYPE userType) {
        this.userType = userType;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
