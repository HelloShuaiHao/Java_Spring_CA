package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.USER_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;

import java.util.List;

public interface userInterface {
    public User CreateUser(User user);
    public List<User> GetAll();
    public void DeleteUserById(int id);
    public User ModifyUserRole(Integer userId, USER_TYPE user_type);

    public User GetUserById(int id);
}
