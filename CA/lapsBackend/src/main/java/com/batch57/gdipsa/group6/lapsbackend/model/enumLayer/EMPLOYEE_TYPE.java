package com.batch57.gdipsa.group6.lapsbackend.model.enumLayer;

public enum EMPLOYEE_TYPE {
    ADMINISTRATIVE(14),
    PROFESSIONAL(18);

    private int annualLeave;

    EMPLOYEE_TYPE(int annualLeave) {
        this.annualLeave = annualLeave;
    }

    public int getAnnualLeave() {
        return annualLeave;
    }

    public void setAnnualLeave(int annualLeave) {
        this.annualLeave = annualLeave;
    }
}
