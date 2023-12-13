package com.batch57.gdipsa.group6.lapsbackend.model.user.employee.leave;

import com.batch57.gdipsa.group6.lapsbackend.model.user.enumLayer.EMPLOYEE_LEAVE_TYPE;

public class EmployeeLeave {
    protected EMPLOYEE_LEAVE_TYPE employeeLeaveType;

    public EmployeeLeave() {
    }

    public EmployeeLeave(EMPLOYEE_LEAVE_TYPE employeeLeaveType) {
        this.employeeLeaveType = employeeLeaveType;
    }

    public EMPLOYEE_LEAVE_TYPE getEmployeeLeaveType() {
        return employeeLeaveType;
    }

    @Override
    public String toString() {
        return "Your current leave type is {"
                + employeeLeaveType +
                '}';
    }
}
