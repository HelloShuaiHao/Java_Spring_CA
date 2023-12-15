package com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.USER_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Employee extends User {
    // 默认为true
    private boolean entitlementToAnnualLeave;

    @JsonIgnore
    @JsonProperty("belongToDepartment") // 对于这种reference外table的property，默认是不会打印的
    @ManyToOne
    private Department belongToDepartment;

    @JsonIgnore // 防止无限recursion
    @OneToMany(mappedBy = "ledByManager", cascade = CascadeType.ALL)
    private Set<Department> leadDepartments;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Application> applications;

    public void setBelongToDepartment(Department belongToDepartment) {
        this.belongToDepartment = belongToDepartment;
    }

    public Employee() {
        this.entitlementToAnnualLeave = true;
    }

    public Employee(String name, String password, USER_TYPE userType) {
        super(name, password, userType);
        this.entitlementToAnnualLeave = true;
    }

    public boolean isEntitlementToAnnualLeave() {
        return entitlementToAnnualLeave;
    }

    public void setEntitlementToAnnualLeave(boolean entitlementToAnnualLeave) {
        this.entitlementToAnnualLeave = entitlementToAnnualLeave;
    }
}
