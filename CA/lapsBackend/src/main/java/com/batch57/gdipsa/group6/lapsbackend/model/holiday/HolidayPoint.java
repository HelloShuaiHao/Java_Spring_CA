package com.batch57.gdipsa.group6.lapsbackend.model.holiday;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.HOLIDAY_POINT_COMPONENT;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class HolidayPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    private HOLIDAY_POINT_COMPONENT component;


    public HolidayPoint() {
    }

    public HolidayPoint(LocalDate date, HOLIDAY_POINT_COMPONENT component) {
        this.date = date;
        this.component = component;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HOLIDAY_POINT_COMPONENT getComponent() {
        return component;
    }

    public void setComponent(HOLIDAY_POINT_COMPONENT component) {
        this.component = component;
    }
}
