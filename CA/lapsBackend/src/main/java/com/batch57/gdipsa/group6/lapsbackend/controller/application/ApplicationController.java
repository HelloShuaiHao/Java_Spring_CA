package com.batch57.gdipsa.group6.lapsbackend.controller.application;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.APPLICATION_STATUS;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.EMPLOYEE_LEAVE_TYPE;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.MEDICAL_LEAVE_MAXIMUM;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.OVERWORKING_UNIT;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.CompensationLeaveValidator;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.aspectj.runtime.internal.Conversions.booleanValue;

/**
 * 可以在JSON中使用ISO 8601日期时间格式，这是标准的日期时间表示方法，
 * 通常被Java的LocalDateTime所接受。ISO 8601的日期时间格式类似于这样："2023-12-31T23:59:59"
 *
 * {
 *     "fromDate": "2023-12-20",
 *     "toDate": "2023-12-25",
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
    @Autowired
    private DepartmentInterfaceImplementation departmentService;

    // compensation检查器
    /**
     * 这个检测器是：如果申请里是Compensation leave, 那么起始点也是必须要填的
     */
    @Autowired
    private CompensationLeaveValidator compensationLeaveValidator;
    @InitBinder
    private void initCompensationLeaveBinder(WebDataBinder binder) {
        binder.addValidators((Validator) compensationLeaveValidator);
    }

    /**
     * 列出所有数据库里的申请
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
     * 创建一个Application需要一个Employee对象(user_id), 1个时间(对输入进行解析)，一个EmployeeLeaveType,
     * 如果是Compensation 的话，还需要声明起始点
     */
    @PostMapping("/create/{user_id}")
    public ResponseEntity<?> CreateApplication(@PathVariable("user_id") int user_id, @Valid @RequestBody Application inApplication, BindingResult bindingResult) {
        // user_id不存在
        Employee employee = employeeService.GetEmployeeById(user_id);
        if(employee == null) return new ResponseEntity<>("ERROR in binding employee", HttpStatus.NOT_FOUND);

        // 没有上级的人不允许发起申请
        Employee superior = employeeService.GetSuperior(user_id);
        if(superior == null) return new ResponseEntity<>("You dont't have a superior, submitting an application is not allowed", HttpStatus.EXPECTATION_FAILED);

        // 申请信息有错误
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            String errMsg= "error in binding Application";
            return new ResponseEntity<>(bindingResult , HttpStatus.EXPECTATION_FAILED);
        }

        // 尝试构建新的申请表格
        Application newApplication = new Application(employee, inApplication.getFromDate(), inApplication.getDayOff(), inApplication.getEmployeeLeaveType());
        newApplication.setCompensationStartPoint(inApplication.getCompensationStartPoint());

        // 检查该用户有无申请资格
        boolean isApplicable = booleanValue(CheckIfEmployeeIsApplicable(user_id).getBody()); // 检查有无已经提交的申请
        if(!isApplicable){
            return new ResponseEntity<>("You have alive application waited to be viewed, please contact your direct manager", HttpStatus.EXPECTATION_FAILED);
        }

        // 检查用户是否有资格申请annual leave
        isApplicable = newApplication.getEmployeeLeaveType() == EMPLOYEE_LEAVE_TYPE.ANNUAL_LEAVE? isApplicable && employee.isEntitlementToAnnualLeave() : isApplicable; // 检查是否entitled to annul leave
        if(!isApplicable) {
            return new ResponseEntity<>("Your are not applicable to annual leave because you have already applied for it.", HttpStatus.EXPECTATION_FAILED);
        }
        // 如果有资格申请annual leave的话，检查是否超过最大允许年假时间
        if(newApplication.getEmployeeLeaveType() == EMPLOYEE_LEAVE_TYPE.ANNUAL_LEAVE) {
            int maximum_annual_leave = employee.getEmployeeType().getAnnualLeave();
            if(maximum_annual_leave < newApplication.getDayOff()) {
                return new ResponseEntity<>("Your maximum annual leave is " + maximum_annual_leave + " days", HttpStatus.EXPECTATION_FAILED);
            }
        }

        // 如果是compensation 的话，检查有无超过最大的允许unit：overworking/unit_time
        if(newApplication.getEmployeeLeaveType()==EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE && isExceedMaximumCompensationUnit(employee, newApplication)){
            return new ResponseEntity<>("Day off exceeds maximum compensation units allowed.", HttpStatus.EXPECTATION_FAILED);
        }

        // 检查medical leave related的
        if(newApplication.getEmployeeLeaveType() == EMPLOYEE_LEAVE_TYPE.MEDICAL_LEAVE) {
            if(newApplication.getDayOff()+employeeService.GetEmployeeMedicalLeave(user_id)/OVERWORKING_UNIT.UNIT.getValue() > MEDICAL_LEAVE_MAXIMUM.MAXIMUM.getValue()) {
                String errMsg = "Medical leave can't exceed " + MEDICAL_LEAVE_MAXIMUM.MAXIMUM.getValue() + " in a calendar year";
                return new ResponseEntity<>(errMsg,HttpStatus.EXPECTATION_FAILED);
            }
        }

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
    public ResponseEntity<?> DeleteApplicationById(@PathVariable("application_id") int application_id) {
        Application application = applicationService.GetApplicationById(application_id);

        // 通过了就不能删除
        if(application.getApplicationStatus() == APPLICATION_STATUS.APPROVED || application.getApplicationStatus() == APPLICATION_STATUS.CANCELLED || application.getApplicationStatus() == APPLICATION_STATUS.REJECTED) {
            return new ResponseEntity<>("Current status " + application.getApplicationStatus() + " can't be deleted",  HttpStatus.EXPECTATION_FAILED);
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
    @GetMapping("/get-application-by-employee-id/{user_id}")
    public ResponseEntity<List<Application>> GetApplicationByEmployeeId(@PathVariable("user_id") int user_id) {
        List<Application> applications = applicationService.GetApplicationByEmployeeId(user_id);
        if(applications == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
    }

    /**
     * 返回某个用户某个状态的所有申请
     * @param user_id
     * @param status
     * @return
     */
    @GetMapping("/get-application-by-employee-id-status/{user_id}/{status}")
    public ResponseEntity<List<Application>> GetApplicationByEmployeeIdAndStatus(@PathVariable("user_id") int user_id, @PathVariable("status") APPLICATION_STATUS status) {
        List<Application> applications = GetApplicationByEmployeeId(user_id).getBody();
        applications = applications
                .stream()
                .filter(a -> a.getApplicationStatus()==status)
                .collect(Collectors.toList());

        return new ResponseEntity<>(applications, HttpStatus.OK);
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

    /**
     * 会把status自动解析成APPLICATION_STATUS，
     * 如果失败直接返回失败
     * @param status
     * @return
     */
    @GetMapping("/get-application-by-department-id-status/{department_id}/{status}")
    public ResponseEntity<List<Application>> GetApplicationByDepartmentIdAndStatus(@PathVariable("department_id") int department_id, @PathVariable("status") APPLICATION_STATUS status) {
        List<Application> applications = GetApplicationByDepartmentId(department_id).getBody();

        applications = applications
                .stream()
                .filter(a -> a.getApplicationStatus()==status)
                .collect(Collectors.toList());

        return new ResponseEntity<>(applications, HttpStatus.OK);
    }


    /**
     * 检查某个用户是否有资格提交申请
     * 判断条件是有没有活跃的申请[appiled/updated]
     * @param user_id
     * @return
     */
    @GetMapping("/is-applicable/{user_id}")
    public ResponseEntity<Boolean> CheckIfEmployeeIsApplicable(@PathVariable("user_id") int user_id ) {
        List<Application> applications = GetApplicationByEmployeeId(user_id).getBody();
        boolean isApplicable = applications
                .stream()
                .anyMatch(a -> a.getApplicationStatus()==APPLICATION_STATUS.APPLIED || a.getApplicationStatus()==APPLICATION_STATUS.UPDATED);

        return ResponseEntity.ok(!isApplicable);
    }

    @GetMapping("/get-subordinates-alive-application")
    public ResponseEntity<?> GetSubordinatesAliveApplication(@RequestHeader("user_id") int user_id) {
        return new ResponseEntity<>(GetAliveApplicationLedBy(user_id), HttpStatus.OK);
    }

    /**
     * 以下逻辑处理一个employee最多能申请几个compensation leave unit
     */
    public Boolean isExceedMaximumCompensationUnit(Employee employee, Application application) {

        // 获取他的overworking时长
        Integer employee_overworking_hour = employee.getOverworkingHour();
        // 计算它有几个unit
        Integer compensation_unit = employee_overworking_hour/ OVERWORKING_UNIT.UNIT.getValue();
        // 这里默认申请compensation的dayOFF的day的意思是half day
        if(application.getDayOff() > compensation_unit) {
            return true;
        }

        return false;
    }

    /**
     * 返回某个人领导下的所有申请
     */
    public List<Application> GetAliveApplicationLedBy(int user_id){
        Employee employee = employeeService.GetEmployeeById(user_id);

        // 判断领导哪个部门
        Integer leads_department = employeeService.isManager(user_id);
        if(leads_department == -1) {
            return null;
        }

        // 获取手下所有员工 + 子部门的领导
        List<Employee> employees = departmentService.GetEmployeesAndSubManagerByDepartmentId(leads_department);
        Set<Integer> idSet = new HashSet<>();
        employees
                .forEach(e -> {
                    idSet.add(e.getUser_id());
                });

        // 筛选这些员工的alive application
        List<Application> allApplications = applicationService.GetAllApplication();
        allApplications= allApplications
                .stream()
                .filter( a -> idSet.contains(a.getEmployee().getUser_id()) && a.getEmployee().getUser_id()!=user_id)
                .toList();

        // 筛选里面的活跃请求
        allApplications = allApplications
                .stream()
                .filter(a -> a.getApplicationStatus()==APPLICATION_STATUS.APPLIED || a.getApplicationStatus()==APPLICATION_STATUS.UPDATED)
                .toList();

        return allApplications;
    }

    /**
     * 返回的申请里不包括cancelled 和 approved 和 rejected
     * @param user_id
     * @return
     */
    @GetMapping("/get-application-waited-to-be-viewed-by")
    public ResponseEntity<?> GetApplicationWaitedToBeViewedByUserId(@RequestHeader("user_id") int user_id) {
        List<Application> applications = applicationService.GetApplicationWaitedToBeViewedByUserId(user_id);
        applications = applications
                .stream()
                .filter(a -> a.getApplicationStatus()==APPLICATION_STATUS.APPLIED || a.getApplicationStatus()==APPLICATION_STATUS.UPDATED)
                .toList();
        if(applications == null) {
            return new ResponseEntity<>("There is no alive application waited to be viewed", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
    }

    /**
     * 返回的申请里不包括cancelled 和 approved 和 rejected
     * @param user_id
     * @return
     */
    @GetMapping("/get-approved-application-to-be-viewed-by")
    public ResponseEntity<?> GetApprovedApplicationToBeViewedByUserId(@RequestHeader("user_id") int user_id) {
        List<Application> applications = applicationService.GetApplicationWaitedToBeViewedByUserId(user_id);
        applications = applications
                .stream()
                .filter(a -> a.getApplicationStatus()==APPLICATION_STATUS.APPROVED)
                .toList();
        if(applications == null) {
            return new ResponseEntity<>("There is no alive application waited to be viewed", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
    }

}
