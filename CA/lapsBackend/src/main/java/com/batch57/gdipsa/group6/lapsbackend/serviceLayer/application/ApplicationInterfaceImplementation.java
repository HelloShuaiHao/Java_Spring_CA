package com.batch57.gdipsa.group6.lapsbackend.serviceLayer.application;

import com.batch57.gdipsa.group6.lapsbackend.interfaceLayer.application.applicationInterface;
import com.batch57.gdipsa.group6.lapsbackend.model.application.Application;
import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import com.batch57.gdipsa.group6.lapsbackend.repository.application.applicationRepository;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.employeeInterfaceImpl;
import com.batch57.gdipsa.group6.lapsbackend.serviceLayer.user.userInterfaceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ApplicationInterfaceImplementation implements applicationInterface {
    @Autowired
    applicationRepository applicationRepo;
    @Autowired
    userInterfaceImpl userService;
    @Autowired
    employeeInterfaceImpl employeeService;
    @Autowired
    ApplicationInterfaceImplementation applicationService;

    @Override
    public Application CreateNewApplication(Application application) {
        return applicationRepo.save(application);
    }

    @Override
    public List<Application> GetAllApplication() {
        return applicationRepo.findAll();
    }

    @Override
    public Application GetApplicationById(int id) {
        return applicationRepo.findById(id).get();
    }

    @Override
    public Application DeleteApplicationById(int id) {
        applicationRepo.deleteById(id);
        Optional<Application> updated = applicationRepo.findById(id);
        if(updated.isPresent()) {
            return updated.get();
        }else {
            return null;
        }
    }

    @Override
    public Application UpdateApplicationById(int id, Application application) {
        Application updated = applicationRepo.findById(id).get();
        if(updated != null) {
            updated.setFromDate(application.getFromDate());
            updated.setDayOff(application.getDayOff());
            updated.setEmployeeLeaveType(application.getEmployeeLeaveType());

            applicationRepo.save(updated);
        }

        updated = applicationRepo.findById(id).get();
        return updated;
    }

    @Override
    public List<Application> GetApplicationByEmployeeId(int user_id) {
        return applicationRepo.GetApplicationByEmployeeId(user_id);
    }

    @Override
    public List<Application> GetApplicationByDepartmentId(int department_id) {
        List<Employee> employees = employeeService.GetAll();
        List<Application> applications = new ArrayList<>();

        employees.stream().forEach(e -> {
             applicationRepo.GetApplicationByEmployeeId(e.getUser_id()).stream().forEach(a -> {
                 applications.add(a);
             });
        });

        return applications;
    }

    @Override
    public Application UpdateApplication(Application application) {
        return applicationRepo.save(application);
    }

    /**
     * 返回所有需要user_id审批的申请，既然需要他审批，说明受到他的直接管理，而不是越级。
     * @param user_id
     * @return
     */
    @Override
    public List<Application> GetApplicationWaitedToBeViewedByUserId(int user_id) {
        // 获取所有的申请
        List<Application> applications = applicationService.GetAllApplication();

        // 筛选需要user_id审批的申请
        // 默认禁止老板发起申请，其他每个人都有上级
        applications = applications
                .stream()
                .filter(a -> employeeService.GetSuperior(a.getEmployee().getUser_id()).getUser_id() == user_id)
                .toList();

        return applications;
    }
}
