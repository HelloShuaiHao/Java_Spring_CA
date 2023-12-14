package com.batch57.gdipsa.group6.lapsbackend.model.user.employee.leave;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.EMPLOYEE_LEAVE_TYPE;

public class EmployeeCompensationLeave extends EmployeeLeave{
    public EmployeeCompensationLeave() {
        super(EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE);
    }

    @Override
    public String toString() {
        return "Compensation Leave" + super.toString();
    }
}
