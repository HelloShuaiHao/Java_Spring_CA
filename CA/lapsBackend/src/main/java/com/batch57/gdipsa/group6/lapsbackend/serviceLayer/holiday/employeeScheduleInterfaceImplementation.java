package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.holiday;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.holiday.employeeScheduleInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;
import com.batch57.gdipsa.group6.lapsbackend.repository.holiday.employeeScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class employeeScheduleInterfaceImplementation implements employeeScheduleInterface {
    @Autowired
    employeeScheduleRepository employeeScheduleRepo;

    /**
     * 给定employeeSchedule 的id 返回这个对象
     * @param id
     * @return
     */
    @Override
    public EmployeeSchedule GetScheduleById(int id) {
        return employeeScheduleRepo.findById(id).get();
    }

    /**
     * 返回一个user_id的所有假期安排
     * @param user_id
     * @return
     */
    @Override
    public List<EmployeeSchedule> GetScheduleByEmployeeId(int user_id) {
        List<EmployeeSchedule> employeeSchedules = employeeScheduleRepo.findAll();
        employeeSchedules = employeeSchedules
                .stream()
                .filter(schedule -> schedule.getEmployee().getUser_id() == user_id)
                .toList();
        return employeeSchedules;
    }

    /**
     * 创建一个新employeeSchedule
     * @param schedule
     * @return
     */
    @Override
    public EmployeeSchedule CreateSchedule(EmployeeSchedule schedule) {
        return employeeScheduleRepo.save(schedule);
    }


    @Override
    public void DeleteSchedule(EmployeeSchedule schedule) {
        // 先删除对外键的引用
        schedule.setEmployee(null);

        employeeScheduleRepo.delete(schedule);
    }

    @Override
    public void  DeleteScheduleById(int id) {
        employeeScheduleRepo.deleteById(id);
    }

    @Override
    public EmployeeSchedule UpdateSchedule(EmployeeSchedule schedule) {
        return employeeScheduleRepo.save(schedule);
    }
}
