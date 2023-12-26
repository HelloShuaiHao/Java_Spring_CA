package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.user.employeeInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.department.Department;
import com.batch57.gdipsa.group6.lapsbackend.model.enumLayer.*;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.EmployeeSchedule;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.HolidayPoint;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.PrivateHoliday;
import com.batch57.gdipsa.group6.lapsbackend.model.holiday.PublicHoliday;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.holiday.privateHolidayRepository;
import com.batch57.gdipsa.group6.lapsbackend.repository.user.employeeRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application.ApplicationInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.department.DepartmentInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.holiday.employeeScheduleInterfaceImplementation;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.holiday.publicHolidayInterfaceImplementation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class employeeInterfaceImpl implements employeeInterface {
    @Autowired
    private employeeRepository repo;
    @Autowired
    private userInterfaceImpl userService;
    @Autowired
    private employeeInterfaceImpl employeeService;
    @Autowired
    private DepartmentInterfaceImplementation departmentService;
    @Autowired
    private ApplicationInterfaceImplementation applicationService;
    @Autowired
    private employeeScheduleInterfaceImplementation employeeScheduleService;
    @Autowired
    private publicHolidayInterfaceImplementation publicHolidayService;
    @Autowired
    private privateHolidayRepository privateHolidayRepository;

    @Override
    public void CreateEmployee(Employee employee) {
        repo.save(employee);
    }

    @Override
    public Employee GetEmployeeById(int id) {
        return repo.findById(id).get();
    }

    @Override
    public Employee UpdateEntitlement(int user_id, boolean entitled) {
        repo.UpdateEntitlement(user_id, entitled);
        return GetEmployeeById(user_id);
    }

    @Override
    public List<Employee> GetAll() {
        return repo.findAll();
    }

    @Override
    public Integer GetEmployeeMedicalLeave(int user_id) {
        return repo.GetEmployeeMedicalLeave(user_id);
    }

    @Override
    public Integer GetOverworkingHourById(int user_id) {
        return repo.GetOverworkingHourById(user_id);
    }

    @Override
    public Employee IncrementOverworkingHour(int user_id, int increment_hour) {
        Employee employee = GetEmployeeById(user_id);
        employee.setOverworkingHour(employee.getOverworkingHour()+increment_hour);
        return repo.save(employee);
    }

    @Override
    public Integer isManager(int user_id) {
        // 需要先获得employee所在的department
        // 需要先获得employee所在的department
        Employee employee = employeeService.GetEmployeeById(user_id);
        Department department = employee.getBelongToDepartment();
        Employee manager = department.getLedByManager();

        if (Objects.equals(employee.getUser_id(), manager.getUser_id())) {
            return department.getId();
        }else {
            return -1;
        }
    }

    @Override
    public Employee GetSuperior(int user_id) {
        int department_id = employeeService.GetEmployeeDepartmentId(user_id);

        int department_manager_id = departmentService.GetDepartmentById(department_id).getLedByManager().getUser_id();

        Department cur = departmentService.GetDepartmentById(department_id);

        if(department_manager_id == user_id) {
            // 当前查询的用户所在的部门的领导是这个用户 返回它的上一级领导
            // 当前部门是被哪个更大的部门给include的
            Department includedByDepartment = cur.getIncludedBy();
            if(includedByDepartment == null){
                // 老板没有上级 返回失败
                return null;
            }
            // 返回更高部门的领导者
            return includedByDepartment.getLedByManager();
        }else {
            // 不是所在部门的领导，直接返回所在部门的领导
            return cur.getLedByManager();
        }
    }

    @Override
    public Integer GetEmployeeDepartmentId(int user_id) {
        return employeeService.GetEmployeeById(user_id).getBelongToDepartment().getId();
    }

    @Override
    public Employee UpdateEmployee(Employee employee) {
        return repo.save(employee);
    }

    @Override
    public Application UpdateApplicationStatus(int application_id, APPLICATION_STATUS newStatus) {
        Application application = applicationService.GetApplicationById(application_id);
        application.setApplicationStatus(newStatus);
        return applicationService.UpdateApplication(application);
    }

    /**
     * 根据用户id，返回除了公共假期之外的日期set，对于可能重合的部分采用覆盖策略
     * @return
     */
    @Override
    public Set<LocalDate> GetEmployeeHolidaySet(int user_id) {
        List<EmployeeSchedule> employeeSchedules = employeeScheduleService.GetScheduleByEmployeeId(user_id);

        Set<LocalDate> localDateSet = new HashSet<>();

//        employeeSchedules
//                .forEach(s -> {
//                    LocalDate start = s.getStart().getDate();
//                    LocalDate end = s.getEnd().getDate();
//                    while (!start.isAfter(end)) {
//                        localDateSet.add(start);
//                        start = start.plusDays(1);
//                    }
//                });

        /**
         * 上面这个算法是不够准确的，真正准确的是需要参考application里的private holiday, 因为可能内部包裹的假期被取消了
         */
        List<Application> applications = applicationService.GetApplicationByEmployeeId(user_id);
        // 过滤已经通过的申请
        applications = applications
                .stream()
                .filter(a -> a.getApplicationStatus()==APPLICATION_STATUS.APPROVED)
                .collect(Collectors.toList());
        applications
                .forEach(a -> {
                    a.getPrivateHolidays()
                            .forEach(p -> {
                                localDateSet.add(p.getDate());
                            });
                });

        return localDateSet;
    }

    /**
     * 返回对于某个用户，某个日期是否和其他假期产生冲突
     * @param user_id
     * @param date
     * @return
     */
    @Override
    public Boolean IsDateApplicableToEmployee(int user_id, LocalDate date) {
        Employee employee = employeeService.GetEmployeeById(user_id);

        //获取员工自己schedule的日期set
        Set<LocalDate> employeeScheduleDateSet = employeeService.GetEmployeeHolidaySet(user_id);

        // 获取公共假期日期set
        Set<LocalDate> publicHolidayDateSet = publicHolidayService.GetPublicHolidaySet();

        // merge
        employeeScheduleDateSet.addAll(publicHolidayDateSet);

        return !employeeScheduleDateSet.contains(date);
    }

    /**
     * 重要逻辑
     * @param application
     * @return
     */
    @Override
    public HolidayPoint GetEndHolidayPointBasedOnApplication(Application application) {
        HolidayPoint holidayPoint;
        LocalDate estimatedToDate;
        HOLIDAY_POINT_COMPONENT holidayPointComponent;

        // 获取基本信息
        LocalDate fromDate = application.getFromDate();
        EMPLOYEE_LEAVE_TYPE employeeLeaveType = application.getEmployeeLeaveType();
        Integer dayOff = application.getDayOff();
        Employee employee = application.getEmployee();

        // 既然这个方法被调用，说明开始时间是没有假期占用的，是合法的

        // 获取员工目前所有的假期date
        Set<LocalDate> curHolidayDate = employeeService.GetEmployeeHolidaySet(employee.getUser_id());
        curHolidayDate.addAll(publicHolidayService.GetPublicHolidaySet());

        // 计算结束那天的 holidayPointComponent
        if(employeeLeaveType == EMPLOYEE_LEAVE_TYPE.COMPENSATION_LEAVE) {
            COMPENSATION_START_POINT compensationStartPoint = application.getCompensationStartPoint();
            if(compensationStartPoint == COMPENSATION_START_POINT.MORNING) {
                holidayPointComponent = (dayOff%2 == 1) ? HOLIDAY_POINT_COMPONENT.MORNING : HOLIDAY_POINT_COMPONENT.AFTERNOON;
            }else {
                holidayPointComponent = (dayOff%2 == 1) ? HOLIDAY_POINT_COMPONENT.AFTERNOON : HOLIDAY_POINT_COMPONENT.MORNING;
            }
            // 把compensation leavde的half day归一, 也就是说计算需要几天才能满足放n个半天
            // 基础的day, 也就是说最理想的情况下，最少，所需要消耗的天数
            dayOff = (dayOff+1)/2;
            // 如果是从下午开始放的，并且在一个上午结束，那么消耗会+1，因为是2的倍数, 但是从下午开始的，一天装不下
            if(compensationStartPoint == COMPENSATION_START_POINT.AFTERNOON && holidayPointComponent == HOLIDAY_POINT_COMPONENT.MORNING) {
                dayOff = dayOff + 1;
            }
        }else {
            holidayPointComponent = HOLIDAY_POINT_COMPONENT.AFTERNOON;
        }


        //计算如果想要放dayOFF天，排期会到哪里
        estimatedToDate = application.getFromDate();

        PrivateHoliday newPrivateHoliday = new PrivateHoliday(estimatedToDate, application);
        if(!application.isPrivateHolidayProcessed()) {
            privateHolidayRepository.save(newPrivateHoliday);
        }

        System.out.println("day off: " + dayOff);
        while(dayOff > 1) {
            // 第一天是肯定没有占用的，不然也不会进入这个方法，等于说进入这个循环，就是从第二天开始，所以直接+1
            estimatedToDate =  estimatedToDate.plusDays(1); // 这里不会改变原来的对象，所以要新赋值

            if(curHolidayDate.contains(estimatedToDate)) {
                // 发现了占用 do nothing

            }else {
                // 没有占用
                dayOff--;
                if(!application.isPrivateHolidayProcessed()) {
                    // 这里括号括起来是为了避免二次执行，因为有两个地方会调用它
                    // 把真正需要放假的那一天加到private holiday里，在把private holiday加到application里面
                    newPrivateHoliday = new PrivateHoliday(estimatedToDate, application);
                    System.out.println(estimatedToDate);
                    privateHolidayRepository.save(newPrivateHoliday);
                }
            }
        }

        // 更新数据库里的状态 防止private holiday二次执行
        application.setPrivateHolidayProcessed(true);
        applicationService.UpdateApplication(application);

        return new HolidayPoint(estimatedToDate,holidayPointComponent);
    }

    @Override
    public void DeleteEmployeeById(int user_id) {
        // 如果还没有部门的话
        Employee employee = employeeService.GetEmployeeById(user_id);
        if(employee.getBelongToDepartment() == null) {
            repo.deleteById(user_id);
            return;
        }

        // 先判断是不是一个manager，如果是的话要解除外键依赖
        int department = isManager(user_id);
        if(department != -1) {
            departmentService.DeleteDepartmentManagerById(department);
        }
        repo.deleteById(user_id);
    }
}
