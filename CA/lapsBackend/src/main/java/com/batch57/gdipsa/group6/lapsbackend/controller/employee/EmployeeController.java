package com.batch57.gdipsa.group6.lapsbackend.controller.employee;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.APPLICATION_STATUS;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private employeeInterfaceImpl employeeService;

    @Autowired
    private DepartmentInterfaceImplementation departmentService;

    @Autowired
    private ApplicationInterfaceImplementation applicationService;

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

    /**
     * 让其他类来调用的静态方法，返回一个user_id的department id
     * @param user_id
     * @return
     */
    public Integer GetEmployeeDepartmentId(int user_id) {
        return employeeService.GetEmployeeById(user_id).getBelongToDepartment().getId();
    }

    @GetMapping("/get-superior/{user_id}")
    public ResponseEntity<?> GetSuperior(@PathVariable("user_id") int user_id) {
        int department_id = GetEmployeeDepartmentId(user_id);
        int department_manager_id = departmentService.GetDepartmentById(department_id).getLedByManager().getUser_id();

        Department cur = departmentService.GetDepartmentById(department_id);

        if(department_manager_id == user_id) {
            // 当前查询的用户所在的部门的领导是这个用户 返回它的上一级领导
            // 当前部门是被哪个更大的部门给include的
            Department includedByDepartment = cur.getIncludedBy();
            if(includedByDepartment == null){
                // 老板没有上级 返回失败
                return new ResponseEntity<>("Boss doesn't have a superior", HttpStatus.NOT_FOUND);
            }
            // 返回更高部门的领导者
            return new ResponseEntity<>(includedByDepartment.getLedByManager(), HttpStatus.OK);
        }else {
            // 不是所在部门的领导，直接返回所在部门的领导
            return new ResponseEntity<>(cur.getLedByManager(), HttpStatus.OK);
        }
    }

    @GetMapping("/get-employees-by-department-id/{department_id}")
    public ResponseEntity<?> GetEmployeesByDepartmentId(@PathVariable("department_id") int department_id) {
        Department department = departmentService.GetDepartmentById(department_id);
        if(department == null) {
            // department not found
            return new ResponseEntity<>("The department you are looking for is not found", HttpStatus.NOT_FOUND);
        }

        List<Employee> employees = departmentService.GetEmployeesByDepartmentId(department_id);
        if(employees.isEmpty()) {
            return new ResponseEntity<>("The department has no employees", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/get-employees-sub-managers-by-department-id/{department_id}")
    public ResponseEntity<?> GetEmployeesAndSubManagerByDepartmentId(@PathVariable("department_id") int department_id) {
        Department department = departmentService.GetDepartmentById(department_id);
        if(department == null) {
            // department not found
            return new ResponseEntity<>("The department you are looking for is not found", HttpStatus.NOT_FOUND);
        }

        List<Employee> employees = departmentService.GetEmployeesAndSubManagerByDepartmentId(department_id, 0);
        if(employees.isEmpty()) {
            return new ResponseEntity<>("The department has no employees", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("get-subordinates-by-id/{user_id}")
    public ResponseEntity<?> GetSubordinatesById(@PathVariable("user_id") int id ){
        int department_id = employeeService.isManager(id);
        if(department_id == -1) {
            return new ResponseEntity<>("You are not a manager!" , HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(departmentService.GetEmployeesAndSubManagerByDepartmentId(department_id, 0), HttpStatus.OK);
    }

//    @GetMapping("/update-application-status-by-id")
//    public ResponseEntity<?> UpdateApplicationStatusById(@RequestHeader("user_id") int user_id, @RequestHeader("application_id") int application_id, @RequestHeader("status") APPLICATION_STATUS status, @RequestBody Application newApplication) {
//        Application application = applicationService.GetApplicationById(application_id);
//
//        List<Application> applications = applicationService.GetApplicationByEmployeeId(user_id);
//        boolean isContained = applications
//                .stream()
//                .anyMatch(a -> a.getApplication_id() == application_id);
//
//        if(!isContained) {
//            // 想修改的这个application，不属于这个user
//            return new ResponseEntity<>("You can't modify other's record", HttpStatus.EXPECTATION_FAILED);
//        }
//
//        if(status == APPLICATION_STATUS.APPLIED || status == APPLICATION_STATUS.APPROVED || status == APPLICATION_STATUS.CANCELLED || status == APPLICATION_STATUS.REJECTED) {
//            return new ResponseEntity<>("Modify status to " + status.toString() + " is not allowed", HttpStatus.EXPECTATION_FAILED);
//        }else {
//            application.setApplicationStatus(status);
//            return new ResponseEntity<>(applicationService.UpdateApplication(application), HttpStatus.OK);
//        }
//    }

//    @GetMapping("/get-subordinates/{user_id}")
//    public ResponseEntity<?> GetSubordinates(@PathVariable("user_id")int user_id) {
//        Employee employee = employeeService.GetEmployeeById(user_id);
//        Department department = departmentService.GetDepartmentById(employee.getBelongToDepartment().getId());
//
//        if(department.getLedByManager().getUser_id() != user_id) {
//            // 不是manager
//            return new ResponseEntity<>("Can't find a subordinate.", HttpStatus.NOT_FOUND);
//        }else {
//            // 是manager
//            List<Employee> subordinates = new ArrayList<>();
//
//        }
//    }
}
