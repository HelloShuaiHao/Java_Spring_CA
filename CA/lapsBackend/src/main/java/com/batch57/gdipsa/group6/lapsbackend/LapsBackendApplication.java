package com.batch57.gdipsa.group6.lapsbackend;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.HOLIDAY_POINT_COMPONENT;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.HolidayPoint;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.userRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.holiday.employeeScheduleInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class LapsBackendApplication {
    @Autowired
    employeeScheduleInterfaceImplementation employeeScheduleService;
    @Autowired
    employeeInterfaceImpl employeeService;
    @Autowired
    ApplicationInterfaceImplementation applicationService;

    public static void main(String[] args) {
        SpringApplication.run(LapsBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(userRepository userRepo) {
        return args -> {
//            HolidayPoint p1 = new HolidayPoint(LocalDate.of(2018,6,12), HOLIDAY_POINT_COMPONENT.MORNING);
//            HolidayPoint p2 = new HolidayPoint(LocalDate.of(2018, 6, 18), HOLIDAY_POINT_COMPONENT.MORNING);
//            Employee employee = employeeService.GetEmployeeById(1);
//            EmployeeSchedule schedule = new EmployeeSchedule(employee, p1, p2);
//            employeeScheduleService.CreateSchedule(schedule);
//            Application application = applicationService.GetApplicationById(1);
//            EmployeeSchedule schedule = employeeScheduleService.GetScheduleById(3);
//            application.setSchedule(schedule);
//            applicationService.UpdateApplication(application);

            // 尝试删除一个记录
//            EmployeeSchedule schedule = application.getSchedule();
//            application.setSchedule(null); // 先解开application对它的外键依赖
//            applicationService.UpdateApplication(application); // 更新application在数据库里的内容

            employeeScheduleService.DeleteSchedule(schedule);

        };
    }

}
