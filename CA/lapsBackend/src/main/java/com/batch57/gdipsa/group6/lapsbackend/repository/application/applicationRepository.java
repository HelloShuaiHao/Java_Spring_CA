package com.batch57.gdipsa.group6.lapsbackend.repository.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 有些需要定制化的JPQL才能实现
 */
@Repository
public interface applicationRepository extends JpaRepository<Application, Integer> {
    @Query("select a from Application a join a.employee e where e.user_id=:id ")
    List<Application> GetApplicationByEmployeeId(@Param("id") int user_id);
}
