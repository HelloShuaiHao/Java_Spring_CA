package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.EMPLOYEE_LEAVE_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.OVERWORKING_UNIT;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class CompensationLeaveValidator implements Validator {
    @Autowired
    employeeInterfaceImpl employeeService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Application application = (Application) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employeeLeaveType", "errors.employeeLeaveType", "employeeLeaveType is required");
        // 如果为compensation 的话，这个字段不能缺失
        if(application.getEmployeeLeaveType() == EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "compensationStartPoint", "errors.compensationStartPoint", "compensationStartPoint is required");
        }



    }
}
