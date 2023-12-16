package com.batch57.gdipsa.group6.lapsbackend.controller.manager;

import com.batch57.gdipsa.group6.lapsbackend.controller.employee.EmployeeController;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.apache.catalina.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired
    ApplicationInterfaceImplementation applicationService;
    @Autowired
    DepartmentInterfaceImplementation departmentService;
    @Autowired
    employeeInterfaceImpl employeeService;

    /**
     * 验证发过来的用户id是否为一个department的manager
     * 为了方便，不采用动态的验证，因为涉及到session。
     *
     * 需要前端把用户id加在url里。
     */
    @GetMapping("/check-manager/{user_id}")
    public ResponseEntity<?> CheckManager(@PathVariable("user_id") int user_id) {
        Integer isManager = isManager(user_id);
        if(isManager != -1) {
            return new ResponseEntity<>(isManager, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("You are not a manager.", HttpStatus.EXPECTATION_FAILED);
        }
    }

    public Integer isManager(int user_id) {
        // 需要先获得employee所在的department
        Employee employee = employeeService.GetEmployeeById(user_id);
        Department department = employee.getBelongToDepartment();
        Employee manager = department.getLedByManager();

        if (employee.getUser_id() == manager.getUser_id()) {
            return department.getId();
        }else {
            return -1;
        }
    }

    /**
     * 发起更新用户加班时间的请求，首先要保证小时为正数，再确保操作的对象是它自己部门里的, 其次每次通过一个compensation leave以后 需要响应减去4小时的用户加班时间
     */
    @GetMapping("/increment-overworking-by-id/{user_id}/{overworking_hour}")
    public ResponseEntity<?> IncrementOverworkingById(@RequestHeader("manager_id") int manager_id, @PathVariable("user_id") int user_id, @PathVariable("overworking_hour") int hour) {
        int manager_department_id = isManager(manager_id);
        if(manager_department_id == -1) {
            return new ResponseEntity<>("You are not a manager!", HttpStatus.EXPECTATION_FAILED);
        }

        if(user_id == manager_id) {
            return new ResponseEntity<>("You can't modify your own record! Please contact to your leader to modify.", HttpStatus.EXPECTATION_FAILED);
        }

        int employee_department_id = employeeService.GetEmployeeById(user_id).getBelongToDepartment().getId();
        if(manager_department_id != employee_department_id) {
            return new ResponseEntity<>("You don't have the permission to this department.", HttpStatus.EXPECTATION_FAILED);
        }

        if(hour <= 0) return new ResponseEntity<>("Hour must be a positive number", HttpStatus.EXPECTATION_FAILED);

        return new ResponseEntity<>(employeeService.IncrementOverworkingHour(user_id, hour), HttpStatus.OK);
    }


}
