package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.department;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;

import java.util.List;

public interface departmentInterface {
    List<Department> GetAll();
    Department GetDepartmentById(int id);
    Employee GetDepartmentMangerByDepartmentId(int departmentId);
    Department AddSubDepartment(int super_department_id, int sub_department_id);
}
