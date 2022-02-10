package com.sap.sptutorial.hibernate.service;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sap.sptutorial.hibernate.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeCustom {
    Employee findEmployeeByEmail(String email);

  Collection<Employee> findByEmailLike(String domain);

  int countByEmailLike(String domain);

  Collection<Employee> readByFirstNameOrLastNameAllIgnoreCase(String fistName, String lastName);

  @Query("select e from Employee e where mod(e.id,  2) = 0 and e.email like ?1")
  Collection<Employee> findEmployeesWithEvenNumber(String domain);
  
  @Query("select new com.sap.sptutorial.hibernate.service.EmployeeBasic(e.firstName, e.orgUnit.name) from Employee e where e.id = ?1")
  EmployeeBasic findBasicById(Long id);
}
