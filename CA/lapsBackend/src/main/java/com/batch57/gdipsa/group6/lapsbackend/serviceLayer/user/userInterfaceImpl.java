package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.userInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.user.enumLayer.USER_TYPE;
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
        repo.deleteById(id);
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
}
