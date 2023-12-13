package com.batch57.gdipsa.group6.lapsbackend.controller.admin;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private employeeInterfaceImpl employeeService;

    @Autowired
    private DepartmentInterfaceImplementation departmentService;

    @GetMapping("/list")
    public ResponseEntity<List<Employee>> GetAllEmployee() {
        List<Employee> employeeList = employeeService.GetAll();
        if(employeeList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        }
    }

    // /api/employee/set-entitlement/{user_id}/{flag}
    @GetMapping("set-entitlement/{user_id}/{flag}")
    public ResponseEntity<Employee> SetEntitlement(@PathVariable("user_id") int user_id, @PathVariable("flag") String flag) {
        if(flag == "true") {
            employeeService.UpdateEntitlement(user_id, true);
        }else{
            employeeService.UpdateEntitlement(user_id, false);
        }

        Employee employee = employeeService.GetEmployeeById(user_id);

        if(employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        }
    }


    @GetMapping("add-employee/{department_id}/{user_id}")
    public ResponseEntity<Employee> SetEmployeeForDepartment(@PathVariable("department_id") int department_id, @PathVariable("user_id") int user_id ) {
        Department department = departmentService.GetDepartmentById(department_id);
        Employee employee = employeeService.GetEmployeeById(user_id);
        employee.setBelongToDepartment(department);

        Employee updated = employeeService.GetEmployeeById(user_id);
        if(updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }

    }


}
