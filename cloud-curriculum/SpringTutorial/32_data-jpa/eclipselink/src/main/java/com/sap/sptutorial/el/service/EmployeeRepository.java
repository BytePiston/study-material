package com.sap.sptutorial.el.service;

import com.sap.sptutorial.el.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Collection;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeCustom {
  Employee findEmployeeByEmail(String email);

  Collection<Employee> findByEmailLike(String domain);

  int countByEmailLike(String domain);

  Collection<Employee> readByFirstNameOrLastNameAllIgnoreCase(String fistName, String lastName);

  @Query("select e from Employee e where mod(e.id,  2) = 0 and e.email like ?1")
  Collection<Employee> findEmployeesWithEvenNumber(String domain);
}
