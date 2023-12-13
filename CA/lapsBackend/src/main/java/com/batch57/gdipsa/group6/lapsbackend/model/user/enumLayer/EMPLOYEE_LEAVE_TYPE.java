package com.batch57.gdipsa.group6.lapsbackend.model.user.enumLayer;

public enum EMPLOYEE_LEAVE_TYPE {
    Normal(1),
    Compensation(0.5);

    private double value;
    private EMPLOYEE_LEAVE_TYPE(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}