package com.batch57.gdipsa.group6.lapsbackend.model.department;


import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.internal.util.beans.BeanInfoHelper;
import org.springframework.util.CollectionUtils;

import java.beans.beancontext.BeanContext;
import java.util.Set;

@Entity
public class Department {
    @JsonProperty("department_id") // retrieve这个对象的时候，会把这个属性一起返回
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @JsonProperty("led_by_manager_user_id") // retrieve这个对象的时候，会把这个属性一起返回
    @ManyToOne()
    private Employee ledByManager;

    @JsonIgnore
    @OneToMany(mappedBy = "belongToDepartment", cascade = CascadeType.ALL)
    private Set<Employee> employees;


    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min=3,max=20, message = "Name must be 3-20 characters")
    private String name;


    /**
     * 修改公司组织架构，一个Department 可以囊括多个Department
     */
    @JsonIgnore
    @OneToMany(mappedBy = "includedBy", cascade = CascadeType.ALL)
    private Set<Department> includesDepartments;

    @ManyToOne
    private Department includedBy;



    // getter and setter

    public String getName() {
        return name;
    }

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    public void setLedByManager(Employee ledByManager) {
        this.ledByManager = ledByManager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Employee getLedByManager() {
        return ledByManager;
    }

    public Department getIncludedBy() {
        return includedBy;
    }

    public void setIncludedBy(Department includedBy) {
        this.includedBy = includedBy;
    }



}
