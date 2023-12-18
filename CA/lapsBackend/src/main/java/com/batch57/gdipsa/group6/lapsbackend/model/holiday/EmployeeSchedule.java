package com.batch57.gdipsa.group6.lapsbackend.model.holiday;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import jakarta.persistence.*;

@Entity
public class EmployeeSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Employee employee;

    @ManyToOne(cascade = CascadeType.ALL)
    private HolidayPoint start;
    @ManyToOne(cascade = CascadeType.ALL)
    private HolidayPoint end;

    public EmployeeSchedule() {
    }

    public EmployeeSchedule(Employee employee, HolidayPoint start, HolidayPoint end) {
        this.employee = employee;
        this.start = start;
        this.end = end;
    }

    public HolidayPoint getStart() {
        return start;
    }

    public void setStart(HolidayPoint start) {
        this.start = start;
    }

    public HolidayPoint getEnd() {
        return end;
    }

    public void setEnd(HolidayPoint end) {
        this.end = end;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
