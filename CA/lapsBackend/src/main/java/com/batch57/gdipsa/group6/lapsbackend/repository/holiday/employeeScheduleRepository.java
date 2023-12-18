package com.batch57.gdipsa.group6.lapsbackend.repository.holiday;

import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface employeeScheduleRepository extends JpaRepository<EmployeeSchedule, Integer> {

}
