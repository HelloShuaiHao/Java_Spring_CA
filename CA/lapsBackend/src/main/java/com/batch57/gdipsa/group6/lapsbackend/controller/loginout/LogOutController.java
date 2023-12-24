package com.batch57.gdipsa.group6.lapsbackend.controller.loginout;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class LogOutController {
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 创建一个与要删除Cookie相同名称和路径的Cookie，然后将其过期时间设置为0，
        // 这将会删除客户端上的Cookie
        Cookie userIdCookie = new Cookie("CurrentUserId", null);
        userIdCookie.setMaxAge(0);
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);

        Cookie userTypeCookie = new Cookie("CurrentUserType", null);
        userTypeCookie.setMaxAge(0);
        userTypeCookie.setPath("/");
        response.addCookie(userTypeCookie);

        Cookie departmentIdCookie = new Cookie("CurrentDepartmentId", null);
        departmentIdCookie.setMaxAge(0);
        departmentIdCookie.setPath("/");
        response.addCookie(departmentIdCookie);

        return "Logout successful";
    }
}

