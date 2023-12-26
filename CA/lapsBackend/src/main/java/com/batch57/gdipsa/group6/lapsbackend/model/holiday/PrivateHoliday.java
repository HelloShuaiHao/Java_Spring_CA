package com.batch57.gdipsa.group6.lapsbackend.model.holiday;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class PrivateHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    @ManyToOne
    private Application application;

    public PrivateHoliday() {
    }

    public PrivateHoliday(LocalDate date, Application application) {
        this.date = date;
        this.application = application;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
