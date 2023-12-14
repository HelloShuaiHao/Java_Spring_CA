package com.batch57.gdipsa.group6.lapsbackend.model.user.employee.leave;

import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.EMPLOYEE_LEAVE_TYPE;

public class EmployeeMedicalLeave extends EmployeeLeave{
    public EmployeeMedicalLeave() {
        super(EMPLOYEE_LEAVE_TYPE.MEDICAL_LEAVE);
    }

    @Override
    public String toString() {
        return "Medical Leave" + super.toString();
    }
}
