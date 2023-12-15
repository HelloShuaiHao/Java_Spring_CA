package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.employeeInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.employeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class employeeInterfaceImpl implements employeeInterface {
    @Autowired
    private employeeRepository repo;
    @Autowired
    private userInterfaceImpl userService;

    @Override
    public void CreateEmployee(Employee employee) {
        repo.save(employee);
    }

    @Override
    public Employee GetEmployeeById(int id) {
        return repo.findById(id).get();
    }

    @Override
    public Employee UpdateEntitlement(int user_id, boolean entitled) {
        repo.UpdateEntitlement(user_id, entitled);
        return GetEmployeeById(user_id);
    }

    @Override
    public List<Employee> GetAll() {
        return repo.findAll();
    }

    @Override
    public Integer GetEmployeeMedicalLeave(int user_id) {
        return repo.GetEmployeeMedicalLeave(user_id);
    }

    @Override
    public Integer GetOverworkingHourById(int user_id) {
        return repo.GetOverworkingHourById(user_id);
    }
}
