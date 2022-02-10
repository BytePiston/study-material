package com.sap.sptutorial.el.service;

import com.sap.sptutorial.el.domain.Employee;
import java.util.Collection;

public interface EmployeeCustom {
  Collection<Employee> findEmployeesWorkingAt(String department);
}
