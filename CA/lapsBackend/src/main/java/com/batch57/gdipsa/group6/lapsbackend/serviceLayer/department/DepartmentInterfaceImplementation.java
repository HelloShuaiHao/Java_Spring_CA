package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.department.departmentInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.department.departmentRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
@Transactional
public class DepartmentInterfaceImplementation implements departmentInterface {
    @Autowired
    private departmentRepository repo;
    @Autowired
    private employeeInterfaceImpl employeeService;

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

    @Override
    public Department AddSubDepartment(int super_department_id, int sub_department_id) {
        Department superDepartment = GetDepartmentById(super_department_id);
        Department subDepartment = GetDepartmentById(sub_department_id);

        subDepartment.setIncludedBy(superDepartment);
        return repo.save(subDepartment);
    }

    /**
     * 这里有一个recursion，保证这个函数返回的是当前department里的员工，然后向下寻找，直到找不到sub department
     * @param department_id
     * @return
     */
    @Override
    public List<Employee> GetEmployeesByDepartmentId(int department_id) {
        List<Employee> allEmployees = employeeService.GetAll();

        List<Employee> employees = new ArrayList<>();

        List<Employee> filteredEmployees = allEmployees
                .stream()
                .filter(e -> e.getBelongToDepartment().getId() == department_id)
                .toList();
        employees.addAll(filteredEmployees);

        List<Department> allDepartments = repo.findAll();

        // 获取当前模块下的所有子模块，递归停止的条件是子模块为空,
        List<Department> subDepartments = allDepartments
                .stream()
                .filter(d -> d.getIncludedBy()!=null && d.getIncludedBy().getId() ==department_id)
                .toList();

        if(subDepartments.isEmpty()) {
            // base case
        }else {
            subDepartments.forEach(d -> {
                employees.addAll(GetEmployeesByDepartmentId(d.getId()));
            });

        }
        return employees;
    }

    /**
     * 这里会有一个我没办法解决掉 lambda表达式内部不能修改报错
     * @param department_id
     * @return
     */
    @Override
    public List<Employee> GetEmployeesAndSubManagerByDepartmentId(int department_id, int depth) {
        if(depth == 2) return new ArrayList<Employee>();

        // 现获取整个公司所有的员工
        List<Employee> allEmployees = employeeService.GetAll();

        // 创建一个新的可变List 用来返回
        AtomicReference<List<Employee>> employees = new AtomicReference<>(new ArrayList<>());


        // 过滤当前部门的员工
        List<Employee> filteredEmployees = allEmployees
                .stream()
                .filter(e -> e.getBelongToDepartment().getId() == department_id )
                .toList();
        employees.get().addAll(filteredEmployees);

        // 获取所有部门
        List<Department> allDepartments = repo.findAll();

        // 获取当前模块下的所有子模块，递归停止的条件是子模块为空,
        List<Department> subDepartments = allDepartments
                .stream()
                .filter(d -> d.getIncludedBy()!=null && d.getIncludedBy().getId() ==department_id)
                .toList();

        // 进行递归过程
        if(subDepartments.isEmpty()) {
            // base case
        }else {
            subDepartments.forEach(d -> {
                employees.get().addAll(GetEmployeesAndSubManagerByDepartmentId(d.getId(), depth+1));
            });

        }

        if(depth == 1) {
            // 最后过滤，只要当前的员工和更小子部门的领导
            employees.set(employees.get()
                    .stream()
                    .filter(e -> isManager(e.getUser_id()))
                    .toList());
        }

        return employees.get();
    }

    public boolean isManager(int user_id) {
        // 需要先获得employee所在的department
        Employee employee = employeeService.GetEmployeeById(user_id);
        Department department = employee.getBelongToDepartment();
        Employee manager = department.getLedByManager();

        if (employee.getUser_id() == manager.getUser_id()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void DeleteDepartmentManagerById(int department_id) {
        Department department = repo.findById(department_id).get();
        if(department != null) {
            department.setLedByManager(null);
        }
    }
}
