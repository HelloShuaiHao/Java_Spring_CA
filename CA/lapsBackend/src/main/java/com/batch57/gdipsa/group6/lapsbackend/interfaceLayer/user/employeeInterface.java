package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;

import java.util.List;

public interface employeeInterface {
    void CreateEmployee(Employee employee);
    Employee GetEmployeeById(int id);
    Employee UpdateEntitlement(int user_id, boolean entitled);
    List<Employee> GetAll();
}
