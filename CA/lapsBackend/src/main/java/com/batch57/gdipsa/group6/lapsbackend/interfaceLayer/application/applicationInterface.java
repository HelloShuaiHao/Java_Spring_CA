package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;

import java.util.List;

public interface applicationInterface {
    Application CreateNewApplication(Application application);
    List<Application> GetAllApplication();
    Application GetApplicationById(int id);
    Application DeleteApplicationById(int id);
//    Application UpdateFromDateById(int id, LocalDateTime fromDate);
//    Application UpdateToDateById(int id, LocalDateTime toDate);
//    Application UpdateLeaveTypeById(int id, EMPLOYEE_LEAVE_TYPE leaveType);
    Application UpdateApplicationById(int id, Application application);
    List<Application> GetApplicationByEmployeeId(int user_id);
    List<Application> GetApplicationByDepartmentId(int department_id);
}
