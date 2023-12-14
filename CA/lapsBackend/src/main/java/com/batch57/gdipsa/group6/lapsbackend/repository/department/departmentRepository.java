package com.batch57.gdipsa.group6.lapsbackend.repository.department;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface departmentRepository extends JpaRepository<Department, Integer> {
}
