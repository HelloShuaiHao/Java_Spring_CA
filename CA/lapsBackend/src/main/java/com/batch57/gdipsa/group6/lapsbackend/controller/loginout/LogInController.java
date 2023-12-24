package com.batch57.gdipsa.group6.lapsbackend.controller.loginout;


import com.batch57.gdipsa.group6.lapsbackend.model.login.LoginRequest;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.userInterfaceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LogInController {
    @Autowired
    userInterfaceImpl userService;
    @Autowired
    employeeInterfaceImpl employeeService;

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginRequest request, HttpServletResponse response) {
        int user_id = request.getUserId();
        String pwd = request.getPassword();

        if(userService.isTrue(user_id, pwd)) {
            User user = userService.GetUserById(user_id);
            Employee employee = employeeService.GetEmployeeById(user_id);

            // 验证成功，将用户id写入Cookie
            Cookie userIdCookie = new Cookie("CurrentUserId", String.valueOf(user_id));
            userIdCookie.setMaxAge(3600); // 设置Cookie过期时间，单位为秒
            userIdCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(userIdCookie);

            Cookie userTypeCookie = new Cookie("CurrentUserType", String.valueOf(user.getUserType()));
            userTypeCookie.setMaxAge(3600); // 设置Cookie过期时间，单位为秒
            userTypeCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(userTypeCookie);

            Cookie departmentIdCookie = new Cookie("CurrentDepartmentId", String.valueOf(employee.getBelongToDepartment().getId()));
            departmentIdCookie.setMaxAge(3600); // 设置Cookie过期时间，单位为秒
            departmentIdCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(departmentIdCookie);

            return new ResponseEntity<>("Successful", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Failed", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
