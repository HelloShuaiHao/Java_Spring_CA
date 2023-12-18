package com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.holiday;

import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;

import java.util.List;

public interface employeeScheduleInterface {
    public EmployeeSchedule GetScheduleById(int id);
    public List<EmployeeSchedule> GetScheduleByEmployeeId(int user_id);
    public EmployeeSchedule CreateSchedule(EmployeeSchedule schedule);
    public void DeleteSchedule(EmployeeSchedule schedule);
    public void DeleteScheduleById(int id);
    public EmployeeSchedule UpdateSchedule(EmployeeSchedule schedule);
}
