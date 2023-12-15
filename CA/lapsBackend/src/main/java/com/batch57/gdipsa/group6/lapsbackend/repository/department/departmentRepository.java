package com.batch57.gdipsa.group6.lapsbackend.repository.department;

import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface departmentRepository extends JpaRepository<Department, Integer> {
    @Query("select d.ledByManager from Department d where d.id = ?1")
    Employee GetDepartmentMangerByDepartmentId(int departmentId);
}
