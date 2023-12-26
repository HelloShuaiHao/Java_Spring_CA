package com.batch57.gdipsa.group6.lapsbackend;

import com.batch57.gdipsa.group6.lapsbackend.model.holiday.PrivateHoliday;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
public class GetEmployeeHolidaySetTest {
     @Autowired
     employeeInterfaceImpl employeeService;
     @Test
     void GetEmployeeHolidaySetTest() {
         Set<LocalDate> privateHolidays = employeeService.GetEmployeeHolidaySet(7);
         privateHolidays.forEach(System.out::println);
     }
}
