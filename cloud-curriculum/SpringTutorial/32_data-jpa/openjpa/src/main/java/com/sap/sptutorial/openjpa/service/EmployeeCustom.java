package com.sap.sptutorial.openjpa.service;

import com.sap.sptutorial.openjpa.domain.Employee;
import java.util.Collection;

public interface EmployeeCustom {
  Collection<Employee> findEmployeesWorkingAt(String department);
}
