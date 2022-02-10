package com.sap.sptutorial.hibernate.service;

import com.sap.sptutorial.hibernate.domain.Employee;
import java.util.Collection;

public interface EmployeeCustom {
  Collection<Employee> findEmployeesWorkingAt(String department);
}
