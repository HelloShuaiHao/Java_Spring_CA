package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;

import java.util.List;

public interface applicationInterface {
    Application CreateNewApplication(Application application);
    List<Application> GetAllApplication();
    Application GetApplicationById(int id);
    Application DeleteApplicationById(int id);
    Application UpdateApplicationById(int id, Application application);
    List<Application> GetApplicationByEmployeeId(int user_id);
    List<Application> GetApplicationByDepartmentId(int department_id);
    Application UpdateApplication(Application application);
    List<Application> GetApplicationWaitedToBeViewedByUserId(int user_id);
}
