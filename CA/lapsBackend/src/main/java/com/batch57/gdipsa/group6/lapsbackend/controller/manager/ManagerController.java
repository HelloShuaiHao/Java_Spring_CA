package com.batch57.gdipsa.group6.lapsbackend.controller.manager;

import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.*;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.HolidayPoint;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.holiday.employeeScheduleInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired
    ApplicationInterfaceImplementation applicationService;
    @Autowired
    DepartmentInterfaceImplementation departmentService;
    @Autowired
    employeeInterfaceImpl employeeService;
    @Autowired
    employeeScheduleInterfaceImplementation employeeScheduleService;

    /**
     * 验证发过来的用户id是否为一个department的manager
     * 为了方便，不采用动态的验证，因为涉及到session。
     *
     * 需要前端把用户id加在url里。
     */
    @GetMapping("/check-manager/{user_id}")
    public ResponseEntity<?> CheckManager(@PathVariable("user_id") int user_id) {
        Integer isManager = isManager(user_id);
        if(isManager != -1) {
            return new ResponseEntity<>(isManager, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("You are not a manager.", HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * 判断是否为manager
     * @param user_id
     * @return
     */
    public Integer isManager(int user_id) {
        // 需要先获得employee所在的department
        Employee employee = employeeService.GetEmployeeById(user_id);
        Department department = employee.getBelongToDepartment();
        Employee manager = department.getLedByManager();

        if (employee.getUser_id() == manager.getUser_id()) {
            return department.getId();
        }else {
            return -1;
        }
    }

    /**
     * 发起更新用户加班时间的请求，首先要保证小时为正数，再确保操作的对象是它自己部门里的, 其次每次通过一个compensation leave以后 需要响应减去4小时的用户加班时间
     */
    @GetMapping("/increment-overworking-by-id/{user_id}/{overworking_hour}")
    public ResponseEntity<?> IncrementOverworkingById(@RequestHeader("manager_id") int manager_id, @PathVariable("user_id") int user_id, @PathVariable("overworking_hour") int hour) {
        int manager_department_id = isManager(manager_id);
        if(manager_department_id == -1) {
            return new ResponseEntity<>("You are not a manager!", HttpStatus.EXPECTATION_FAILED);
        }

        // 判断是不是在修改自己
        if(user_id == manager_id) {
            return new ResponseEntity<>("You can't modify your own record! Please contact to your leader to modify.", HttpStatus.EXPECTATION_FAILED);
        }

        // 获取当前manager手下所有可以管理的员工
        List<Employee> subordinates = departmentService.GetEmployeesByDepartmentId(manager_department_id);
        boolean isContained = subordinates
                .stream()
                .anyMatch(s -> s.getUser_id()==user_id);
        if(!isContained) {
            return new ResponseEntity<>("You don't have the permission for this employeee.", HttpStatus.OK);
        }

        // 判断小时是不是为正整数
        if(hour <= 0) return new ResponseEntity<>("Hour must be a positive number", HttpStatus.EXPECTATION_FAILED);

        // 调用底层的数据库服务
        return new ResponseEntity<>(employeeService.IncrementOverworkingHour(user_id, hour), HttpStatus.OK);
    }

    /**
     * 更改某个申请的状态
     * 并且在RequestHeader里面有{manager_id} {application_id} 和 {status}, 其中application_status要遵循enum格式,
     * 表示manager_id的用户 要修改application_id的申请状态为status
     */
    @GetMapping("/update-application-status")
    public ResponseEntity<?> UpdateApplicationStatus(@RequestHeader("manager_id") int manager_id, @RequestHeader("application_id") int application_id ,@RequestHeader("status") APPLICATION_STATUS status, @RequestHeader("reviewedComment") String reviewedComment) {
        // 获取发起请求的对象
        Employee manager = employeeService.GetEmployeeById(manager_id);
        if(manager == null) return new ResponseEntity<>("Please input a valid user ID", HttpStatus.NOT_FOUND);

        // 获取想修改的application
        Application application = applicationService.GetApplicationById(application_id);
        EMPLOYEE_LEAVE_TYPE leaveType = application.getEmployeeLeaveType();

        // 获取发起请求的人 所领导的部门 ，如果不是manager返回-1
        Integer leadDepartment = isManager(manager_id);
        if(leadDepartment == -1) return new ResponseEntity<>("You don't have any subordinate", HttpStatus.NOT_FOUND);

        // 获取这个申请所属的员工
        Employee employee = application.getEmployee();

        // 获取这个员工的直接领导者
        Employee superior = employeeService.GetSuperior(employee.getUser_id());
        if(superior == null) {
            // 没有找到直接领导者
            return new ResponseEntity<>("The superior of employee " + employee.getUser_id() + " can't be found", HttpStatus.NOT_FOUND);
        }

        if(superior.getUser_id() != manager_id) {
            return new ResponseEntity<>("You don't have the permission for this employee, please contact the direct manager", HttpStatus.EXPECTATION_FAILED);
        }

        // 尝试修改申请状态

        // 如果状态已经为Cancelled了，那就不能继续其他操作了
        APPLICATION_STATUS curStatus = application.getApplicationStatus();
        if(curStatus == APPLICATION_STATUS.CANCELLED) {
            return new ResponseEntity<>("This application has already been cancelled and can't be moved forward", HttpStatus.EXPECTATION_FAILED);
        }

        // [approved] -> [cancelled]
        if( (curStatus == APPLICATION_STATUS.APPROVED) && status == APPLICATION_STATUS.CANCELLED) {
            application.setApplicationStatus(status);
            application.setReviewedComment(reviewedComment);

            // 如果是compensation leave的话需要重新恢复employee的overworking time
            if(leaveType == EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE) {
                employee.setOverworkingHour(employee.getOverworkingHour() + application.getDayOff()* OVERWORKING_UNIT.UNIT.getValue());

            }

            // 如果是annual leave的话需要重新恢复employee的entitlement
            if(leaveType == EMPLOYEE_LEAVE_TYPE.ANNUAL_LEAVE) {
                employee.setEntitlementToAnnualLeave(true);
            }

            // 如果是medical leave的话需要重新减去它的总medical时间
            if(leaveType == EMPLOYEE_LEAVE_TYPE.MEDICAL_LEAVE) {
                employee.setCalenderYearMedicalLeave(employee.getCalenderYearMedicalLeave() - application.getDayOff());
            }

            employeeService.UpdateEmployee(employee);

            // 撤销一条已经通过的申请，需要在放假安排里删除这个员工的放假安排
            EmployeeSchedule schedule = application.getSchedule(); // 获取这条申请所对应的放假安排
            application.setSchedule(null); // 断开对schedule的外键依赖
            applicationService.UpdateApplication(application); // 在数据库更新
            employeeScheduleService.DeleteSchedule(schedule);

            return new ResponseEntity<>(applicationService.UpdateApplication(application), HttpStatus.OK);
        }

        // 不能重复设置applied
        if( (curStatus == APPLICATION_STATUS.APPLIED || curStatus == APPLICATION_STATUS.UPDATED) && status == APPLICATION_STATUS.APPLIED) {
            return new ResponseEntity<>("One application can't not be set to applied again.", HttpStatus.EXPECTATION_FAILED);
        }

        // [applied/updated] -> [approved/rejected]
        if( (curStatus == APPLICATION_STATUS.APPLIED || curStatus == APPLICATION_STATUS.UPDATED) && (status != APPLICATION_STATUS.CANCELLED)) {
            application.setApplicationStatus(status);
            application.setReviewedComment(reviewedComment);


            // // [applied/updated] -> [approved]
            if(status == APPLICATION_STATUS.APPROVED){
                // 开始的HolidayPoint
                HolidayPoint StartHolidayPoint = new HolidayPoint();

                if(leaveType == EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE){
                    // 判断逻辑，如果通过了一个compensation leave 需要减去该employee的加班时间
                    employee.setOverworkingHour(employee.getOverworkingHour() - application.getDayOff()*OVERWORKING_UNIT.UNIT.getValue());
                    // 设置开始点
                    StartHolidayPoint = new HolidayPoint(application.getFromDate(), application.getCompensationStartPoint()== COMPENSATION_START_POINT.MORNING?HOLIDAY_POINT_COMPONENT.MORNING : HOLIDAY_POINT_COMPONENT.AFTERNOON);
                }
                if(leaveType == EMPLOYEE_LEAVE_TYPE.ANNUAL_LEAVE){
                    // 判断逻辑，如果通过了一个annual leave 需要禁用当年的entitlement to annual leave
                    employee.setEntitlementToAnnualLeave(false);
                    // 设置开始点
                    StartHolidayPoint = new HolidayPoint(application.getFromDate(), HOLIDAY_POINT_COMPONENT.MORNING);
                }
                if(leaveType == EMPLOYEE_LEAVE_TYPE.MEDICAL_LEAVE){
                    // 判断逻辑，如果通过了一个medical leave 需要加上该employee的medical leave时间
                    employee.setCalenderYearMedicalLeave(employee.getCalenderYearMedicalLeave() + application.getDayOff());
                    //设置开始点
                    StartHolidayPoint = new HolidayPoint(application.getFromDate(), HOLIDAY_POINT_COMPONENT.MORNING);
                }

                // 如果通过了一个申请，就要在放假安排里面增加它的放假安排
                // 计算假期结束是哪天的上午还是下午
                HolidayPoint estimatedEndHolidayPoint = employeeService.GetEndHolidayPointBasedOnApplication(application);

                EmployeeSchedule schedule = new EmployeeSchedule(employee, StartHolidayPoint, estimatedEndHolidayPoint); // 这里默认一个开始结尾的时间，因为还没有写交叉日期的算法

                employeeScheduleService.CreateSchedule(schedule); // 保存到假期安排的数据库中

                application.setSchedule(schedule); // 把该application关联到这个schedule中
            }

            // 保存
            employeeService.UpdateEmployee(employee);
            return new ResponseEntity<>(applicationService.UpdateApplication(application), HttpStatus.OK);
        }

        return new ResponseEntity<>(curStatus + " -> " + status + " is not allowed", HttpStatus.EXPECTATION_FAILED);
    }


}
