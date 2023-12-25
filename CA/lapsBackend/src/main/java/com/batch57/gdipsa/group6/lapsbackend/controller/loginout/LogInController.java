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
import jakarta.servlet.http.HttpSession;


@CrossOrigin(origins = { "http://localhost:3000" }, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class LogInController {
    @Autowired
    userInterfaceImpl userService;
    @Autowired
    employeeInterfaceImpl employeeService;

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginRequest request, HttpServletResponse response, HttpSession session) {
        int user_id = request.getUserId();
        String pwd = request.getPassword();

        if(userService.isTrue(user_id, pwd)) {
            User user = userService.GetUserById(user_id);
            Employee employee = employeeService.GetEmployeeById(user_id);

            // 验证成功，将用户id写入Cookie
            Cookie userIdCookie = new Cookie("CurrentUserId", String.valueOf(user_id));
            userIdCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(userIdCookie);

            Cookie userTypeCookie = new Cookie("CurrentUserType", String.valueOf(user.getUserType()));
            userTypeCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(userTypeCookie);
//
            Cookie departmentIdCookie = new Cookie("CurrentDepartmentId", String.valueOf(employee.getBelongToDepartment().getId()));
            departmentIdCookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(departmentIdCookie);

//            session.setAttribute("CurrentUserId", user_id);
//            session.setAttribute("CurrentUserType", user.getUserType());
//            session.setAttribute("CurrentDepartmentId", employee.getBelongToDepartment().getId());

//            response.addCookie(new Cookie("CurrentUserId", String.valueOf(user_id)));
//            response.setHeader("Access-Control-Allow-Credentials", "true");

            return new ResponseEntity<>("Successful", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Failed", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
