package com.batch57.gdipsa.group6.lapsbackend.repository.user;

import com.batch57.gdipsa.group6.lapsbackend.model.user.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface employeeRepository extends JpaRepository<Employee, Integer> {
    @Modifying
    @Query("update Employee e set e.entitlementToAnnualLeave=:entitled where e.user_id=:id")
    void UpdateEntitlement(@Param("id") int id, @Param("entitled") boolean entitled);

    /**
     * 返回某用户的medical leave
     */
    @Query("select e.calenderYearMedicalLeave from Employee e where e.user_id = ?1")
    Integer GetEmployeeMedicalLeave(int user_id);

    @Query("select e.overworkingHour from Employee e where e.user_id = ?1")
    Integer GetOverworkingHourById(int user_id);

}
