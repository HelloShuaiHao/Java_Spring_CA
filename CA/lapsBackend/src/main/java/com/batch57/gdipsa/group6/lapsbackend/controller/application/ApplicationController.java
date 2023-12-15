package com.batch57.gdipsa.group6.lapsbackend.controller.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.APPLICATION_STATUS;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.CompensationLeaveValidator;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
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

    // compensation检查器
    @Autowired
    private CompensationLeaveValidator compensationLeaveValidator;
    @InitBinder
    private void initCompensationLeaveBinder(WebDataBinder binder) {
        binder.addValidators((Validator) compensationLeaveValidator);
    }

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
     * 创建一个Application需要一个Employee对象(user_id), 两个时间(对输入进行解析)，一个EmployeeLeaveType,
     * 如果是Compensation 的话，还需要声明起始点
     */
    @PostMapping("/create/{user_id}")
    public ResponseEntity<Application> CreateApplication(@PathVariable("user_id") int user_id, @Valid @RequestBody Application inApplication, BindingResult bindingResult) {
        // user_id不存在
        Employee employee = employeeService.GetEmployeeById(user_id);
        if(employee == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);



        // 申请信息有错误
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        Application newApplication = new Application(employee, inApplication.getFromDate(), inApplication.getToDate(), inApplication.getEmployeeLeaveType());
        newApplication.setCompensationStartPoint(inApplication.getCompensationStartPoint());

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
     * 根据编号删除application，如果成功，返回被删除对象。如果已经approved了，不能删除
     * @param application_id
     * @return
     */
    @DeleteMapping("/delete/{application_id}")
    public ResponseEntity<Application> DeleteApplicationById(@PathVariable("application_id") int application_id) {
        Application application = applicationService.GetApplicationById(application_id);

        // 通过了就不能删除
        if(application.getApplicationStatus() == APPLICATION_STATUS.APPROVED) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        Application updated = applicationService.DeleteApplicationById(application_id);

        if(updated == null) {
            return new ResponseEntity<>(application, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    /**
     * 返回某个用户id的所有假期申请
     * @param user_id
     * @return
     */
    @GetMapping("/get-application-by-employedd-id/{user_id}")
    public ResponseEntity<List<Application>> GetApplicationByEmployeeId(@PathVariable("user_id") int user_id) {
        List<Application> applications = applicationService.GetApplicationByEmployeeId(user_id);
        if(applications == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
    }

    /**
     * 返回一个 部门的所有申请
     * @param department_id
     * @return
     */
    @GetMapping("/get-application-by-department-id/{department_id}")
    public ResponseEntity<List<Application>> GetApplicationByDepartmentId(@PathVariable("department_id") int department_id) {
        List<Application> applications = applicationService.GetApplicationByDepartmentId(department_id);

        if(applications == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
    }


}
