package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.department;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;

import java.util.List;

public interface departmentInterface {
    List<Department> GetAll();
    Department GetDepartmentById(int id);
}
