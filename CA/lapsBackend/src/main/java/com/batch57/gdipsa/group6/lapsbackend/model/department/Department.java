package com.batch57.gdipsa.group6.lapsbackend.model.department;


import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.model.user.userinfo.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Entity
public class Department {
    @JsonProperty("department_id") // retrieve这个对象的时候，会把这个属性一起返回
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("led_by_manager_user_id") // retrieve这个对象的时候，会把这个属性一起返回
    @ManyToOne
    private Employee ledByManager;

    @OneToMany(mappedBy = "belongToDepartment", cascade = CascadeType.ALL)
    private Set<Employee> employees;


    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min=3,max=20, message = "Name must be 3-20 characters")
    private String name;

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    public void setLedByManager(Employee ledByManager) {
        this.ledByManager = ledByManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
