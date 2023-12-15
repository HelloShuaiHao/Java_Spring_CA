package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.department.departmentInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.department.departmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class DepartmentInterfaceImplementation implements departmentInterface {
    @Autowired
    private departmentRepository repo;

    @Override
    public List<Department> GetAll() {
        return repo.findAll();
    }

    @Override
    public Department GetDepartmentById(int id) {
        return repo.findById(id).get();
    }

    @Override
    public Employee GetDepartmentMangerByDepartmentId(int departmentId) {
        return repo.GetDepartmentMangerByDepartmentId(departmentId);
    }
}
