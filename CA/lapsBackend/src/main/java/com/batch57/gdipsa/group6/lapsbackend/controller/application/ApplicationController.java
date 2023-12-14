package com.batch57.gdipsa.group6.lapsbackend.controller.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 可以在JSON中使用ISO 8601日期时间格式，这是标准的日期时间表示方法，
 * 通常被Java的LocalDateTime所接受。ISO 8601的日期时间格式类似于这样："2023-12-31T23:59:59"
 *
 * {
 *     "fromDate": "2023-12-20T08:00:00",
 *     "toDate": "2023-12-25T17:00:00",
 *     "employeeLeaveType": "ANNUAL_LEAVE"
 * }
 *
 * 如果遇到日期时间格式的问题或者需要自定义日期时间的格式，
 * 可以在Application类中对LocalDateTime字段使用@JsonFormat注解来指定格式
 * @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
 * LocalDateTime fromDate;
 *
 */
@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private employeeInterfaceImpl employeeService;
    @Autowired
    private ApplicationInterfaceImplementation applicationService;

    /**
     * 列出所有元素
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Application>> GetAll() {
        List<Application> applicationList = applicationService.GetAllApplication();
        if(applicationList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applicationList, HttpStatus.OK);
        }
    }

    /**
     * 创建一个Application需要一个Employee对象(user_id), 两个时间(对输入进行解析)，一个EmployeeLeaveType
     */
    @PostMapping("/create/{user_id}")
    public ResponseEntity<Application> CreateApplication(@PathVariable("user_id") int user_id, @RequestBody Application inApplication) {
        Employee employee = employeeService.GetEmployeeById(user_id);

        Application newApplication = new Application(employee, inApplication.getFromDate(), inApplication.getToDate(), inApplication.getEmployeeLeaveType());

        Application created =  applicationService.CreateNewApplication(newApplication);
        if(created == null) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }else {
            return new ResponseEntity<>(created, HttpStatus.OK);
        }
    }


    /**
     * 获取特定的application信息
     *
     * @param application_id
     * application编号
     * @return
     */
    @GetMapping("/get/{application_id}")
    public ResponseEntity<Application> GetApplicationById(@PathVariable("application_id") int application_id) {
        Application application = applicationService.GetApplicationById(application_id);

        if(application == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(application, HttpStatus.OK);
        }
    }

    /**
     * 根据编号删除application，如果成功，返回被删除对象。
     * @param application_id
     * @return
     */
    @DeleteMapping("/delet/{application_id}")
    public ResponseEntity<Application> DeleteApplicationById(@PathVariable("application_id") int application_id) {
        Application application = applicationService.GetApplicationById(application_id);

        Application updated = applicationService.DeleteApplicationById(application_id);
        if(updated == null) {
            return new ResponseEntity<>(application, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


}
