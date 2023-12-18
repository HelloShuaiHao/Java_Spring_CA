package com.batch57.gdipsa.group6.lapsbackend.controller.admin;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.userInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.USER_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.userRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.userInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private userRepository repo;

    @Autowired
    private userInterface userService;

    @Autowired
    private DepartmentInterfaceImplementation departmentService;

    @Autowired
    public void setUserService(userInterfaceImpl impl) {
        this.userService = userService;
    }

    @Autowired
    private employeeInterfaceImpl EmployeeService;

    /**
     * user_type是用户种类
     * 这里会把request body里面的映射到一个类里
     * @param user_type
     * @param inUser
     * @return
     */
    @PostMapping("/create/{user_type}")
    public ResponseEntity<User> createUser(@PathVariable("user_type") int user_type, @RequestBody User inUser) {
        try {
            USER_TYPE[] types = USER_TYPE.values();

            User newUser = new User(inUser.getName(), inUser.getPassword(), types[user_type]);

            Employee emp = new Employee(inUser.getName(), inUser.getPassword(), types[user_type]);
            if(types[user_type] == USER_TYPE.EMPLOYEE) {
                EmployeeService.CreateEmployee(emp);
            }else {
                userService.CreateUser(newUser);
            }

            return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> GetAllUser() {
        List<User> userList = userService.GetAll();
        if (userList != null) {
            return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<User> DeleteById(@PathVariable("id") Integer id) {
        userService.DeleteUserById(id);
        User optUser = repo.getById(id);
        if(optUser == null) {
            return new ResponseEntity<>(optUser, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/update/{id}/{user_type}")
    public ResponseEntity<User> ModifyUserRoleById(@PathVariable("id") int id, @PathVariable("user_type") int user_type) {
        USER_TYPE[] types = USER_TYPE.values();
        User updated = userService.ModifyUserRole(id, types[user_type]);

        if(updated == null) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }else{
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<User> GetUserById(@PathVariable("id") Integer id) {
        User u = userService.GetUserById(id);
        if(u == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(u, HttpStatus.OK);
        }
    }

    /**
     * 先创建用户再创建employee
     * 失败了 在子类表里添加会新增一条记录
     * @param user_id
     * @return
     */
//    @GetMapping("/create-employee-by-id/{user_id}")
//    public ResponseEntity<Employee> CreateEmployee(@PathVariable("user_id") int user_id) {
//        User u = userService.GetUserById(user_id);
//
//        Employee emp = new Employee(u.getName(), u.getPassword(), u.getUserType());
//        emp.setUser_id(u.getUser_id());
//        EmployeeService.CreateEmployee(emp);
//
//        if(u == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }else{
//            return new ResponseEntity<>(emp, HttpStatus.OK);
//        }
//    }

    /**
     * 同时创建user和employee
     * 因为继承的关系，并且是joined，所以直接在employee里添加，user里就会有新的记录
     * @param user_type
     * @param inUser
     * @return
     */
    @PostMapping("/create-user-employee/{user_type}")
    public ResponseEntity<Employee> CreateUserAndEmployee(@PathVariable("user_type") int user_type, @RequestBody User inUser) {
        try {
            USER_TYPE[] types = USER_TYPE.values();

            Employee emp = new Employee(inUser.getName(), inUser.getPassword(), types[user_type]);

            EmployeeService.CreateEmployee(emp);

            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }

    // 应该在新的entity里做 而不是user
//    @GetMapping("/set-belong-to-department/{user_id}/{department_id}")
//    public ResponseEntity<Employee> SetBelongToDepartment(@PathVariable("user_id") Integer user_id, @PathVariable("department_id") Integer department_id) {
//        Department department = departmentService.GetDepartmentById(department_id);
//        Employee employee = (Employee) userService.GetUserById(user_id);
//    }
}
