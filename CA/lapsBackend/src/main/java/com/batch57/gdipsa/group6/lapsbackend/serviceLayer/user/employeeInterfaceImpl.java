package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.employeeInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.employeeRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class employeeInterfaceImpl implements employeeInterface {
    @Autowired
    private employeeRepository repo;
    @Autowired
    private userInterfaceImpl userService;
    @Autowired
    private employeeInterfaceImpl employeeService;
    @Autowired
    private DepartmentInterfaceImplementation departmentService;


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

    @Override
    public Employee IncrementOverworkingHour(int user_id, int increment_hour) {
        Employee employee = GetEmployeeById(user_id);
        employee.setOverworkingHour(employee.getOverworkingHour()+increment_hour);
        return repo.save(employee);
    }

    @Override
    public Integer isManager(int user_id) {
        // 需要先获得employee所在的department
        // 需要先获得employee所在的department
        Employee employee = employeeService.GetEmployeeById(user_id);
        Department department = employee.getBelongToDepartment();
        Employee manager = department.getLedByManager();

        if (Objects.equals(employee.getUser_id(), manager.getUser_id())) {
            return department.getId();
        }else {
            return -1;
        }
    }

    @Override
    public Employee GetSuperior(int user_id) {
        int department_id = employeeService.GetEmployeeDepartmentId(user_id);

        int department_manager_id = departmentService.GetDepartmentById(department_id).getLedByManager().getUser_id();

        Department cur = departmentService.GetDepartmentById(department_id);

        if(department_manager_id == user_id) {
            // 当前查询的用户所在的部门的领导是这个用户 返回它的上一级领导
            // 当前部门是被哪个更大的部门给include的
            Department includedByDepartment = cur.getIncludedBy();
            if(includedByDepartment == null){
                // 老板没有上级 返回失败
                return null;
            }
            // 返回更高部门的领导者
            return includedByDepartment.getLedByManager();
        }else {
            // 不是所在部门的领导，直接返回所在部门的领导
            return cur.getLedByManager();
        }
    }

    @Override
    public Integer GetEmployeeDepartmentId(int user_id) {
        return employeeService.GetEmployeeById(user_id).getBelongToDepartment().getId();
    }

    @Override
    public Employee UpdateEmployee(Employee employee) {
        return repo.save(employee);
    }
}
