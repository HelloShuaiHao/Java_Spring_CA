package com.batch57.gdipsa.group6.lapsbackend.model.user.employee.leave;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.EMPLOYEE_LEAVE_TYPE;

public class EmployeeAnnualLeave extends EmployeeLeave{
    public EmployeeAnnualLeave() {
        super(EMPLOYEE_LEAVE_TYPE.ANNUAL_LEAVE);
    }

    @Override
    public String toString() {
        return "Annual Leave" + super.toString();
    }
}
