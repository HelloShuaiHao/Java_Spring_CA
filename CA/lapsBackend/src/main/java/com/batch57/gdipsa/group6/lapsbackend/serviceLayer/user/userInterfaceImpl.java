package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.userInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.USER_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.userRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class userInterfaceImpl implements userInterface {
    @Autowired
    userRepository repo;
    @Autowired
    employeeInterfaceImpl employeeService;

    @Override
    public User CreateUser(User user) {
        return repo.save(user);
    }

    @Override
    public List<User> GetAll() {
        return repo.findAll();
    }

    @Override
    public void DeleteUserById(int id) {
        User user = repo.findById(id).get();
        if(user.getUserType() == USER_TYPE.EMPLOYEE) {
            employeeService.DeleteEmployeeById(id);
        }else {
            repo.deleteById(id);
        }

    }

    @Override
    public User ModifyUserRole(Integer userId, USER_TYPE user_type) {
        repo.ModifyUserRoleById(userId, user_type);
        return repo.findById(userId).get();
    }

    @Override
    public User GetUserById(int id) {
        return repo.findById(id).get();
    }


    @Override
    public Boolean isTrue(int id, String password) {
        User user = GetUserById(id);

        if(user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }
}
