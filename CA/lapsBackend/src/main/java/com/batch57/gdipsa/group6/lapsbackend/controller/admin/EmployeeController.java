package com.batch57.gdipsa.group6.lapsbackend.controller.admin;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private employeeInterfaceImpl employeeService;

    @Autowired
    private DepartmentInterfaceImplementation departmentService;

    /**
     * 返回employee列表
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Employee>> GetAllEmployee() {
        List<Employee> employeeList = employeeService.GetAll();
        if(employeeList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        }
    }

    /**
     * 创建新employee的同时，添加数据到user数据库中
     */
    @PostMapping("/create")
    public ResponseEntity<Employee> Create(@RequestBody Employee inEmployee) {
        Employee newEmployee = new Employee(inEmployee.getName(), inEmployee.getPassword(), inEmployee.getUserType());
        newEmployee.setEmployeeType(inEmployee.getEmployeeType());

        employeeService.CreateEmployee(newEmployee);
        return new ResponseEntity<>(newEmployee, HttpStatus.OK);
    }

    /**
     * 修改employee annual leave entitlement, 默认为true
     * @param user_id
     * @param flag
     * @return
     */
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

    /**
     * 为部门添加employee
     * @param department_id
     * @param user_id
     * @return
     */
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
