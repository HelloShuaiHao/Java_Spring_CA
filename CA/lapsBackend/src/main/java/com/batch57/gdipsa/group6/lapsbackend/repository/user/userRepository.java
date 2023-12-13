package com.batch57.gdipsa.group6.lapsbackend.repository.user;

import com.batch57.gdipsa.group6.lapsbackend.model.user.enumLayer.USER_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface userRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Query("update User u set u.userType=:newUserType where u.user_id=:userId")
    void ModifyUserRoleById(@Param("userId") int userId, @Param("newUserType") USER_TYPE user_type);
}
