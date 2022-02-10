package com.sap.sptutorial.hibernate.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.hibernate.domain.Employee;
import com.sap.sptutorial.hibernate.domain.OrgUnit;
import com.sap.sptutorial.hibernate.service.EmployeeBasic;
import com.sap.sptutorial.hibernate.service.EmployeeRepository;
import com.sap.sptutorial.hibernate.service.OrgUnitService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class JpaRestController {

  @Autowired
  private OrgUnitService orgUnitService;

  @Autowired
  private EmployeeRepository employeeRepository;

  @RequestMapping(value = "employee/{id}", method = RequestMethod.GET)
  public EmployeeBasic getEmployee(@PathVariable("id") String id) {
    EmployeeBasic employee = employeeRepository.findBasicById(Long.valueOf(id));
    if (employee == null) {
      throw new NotFoundException();
    }
    return employee;
  }

  @RequestMapping(value = "employee", method = RequestMethod.GET)
  public Employee getEmployeeByEmail(@RequestParam("email") String email) {
    Employee employee = employeeRepository.findEmployeeByEmail(email);
    if (employee == null) {
      throw new NotFoundException();
    }
    return employee;
  }

  @RequestMapping(value = "employees/even", method = RequestMethod.GET)
  public Collection<Employee> getEmployeesWithEvenId() {
    return employeeRepository.findEmployeesWithEvenNumber("%");
  }

  @RequestMapping(value = "employees/count", method = RequestMethod.GET)
  public int countEmployeesByDomain(@RequestParam("domain") String domain) {
    log.debug(domain);
    return employeeRepository.countByEmailLike("%" + domain);
  }

  @RequestMapping(value = "employees", method = RequestMethod.GET)
  public Collection<Employee> getEmployeesByFilter(@RequestParam(value = "domain", required = false) String domain,
                                                   @RequestParam(value = "first", required = false) String firstName,
                                                   @RequestParam(value = "last", required = false) String lastName) {
    Collection<Employee> found = null;
    if (domain != null && !domain.isEmpty())
      found = employeeRepository.findByEmailLike("%" + domain);
    else if (firstName != null && !firstName.isEmpty() || lastName != null && !lastName.isEmpty()) {
      found = employeeRepository.readByFirstNameOrLastNameAllIgnoreCase(firstName, lastName);
    } else {
        found = employeeRepository.findAll();
    }
    if (found == null || found.size() == 0)
      throw new NotFoundException();
    return found;
  }

  @RequestMapping(value = "employee", method = RequestMethod.POST)
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    Employee ret = employeeRepository.save(employee);
    return new ResponseEntity<Employee>(ret, HttpStatus.OK);
  }

  @RequestMapping(value = "department/{dept}", method = RequestMethod.GET)
  public Collection<Employee> getEmployeesAtDepartment(@PathVariable("dept") String department) {
    return employeeRepository.findEmployeesWorkingAt(department);
  }

  @RequestMapping(value = "orgunit/{id}", method = RequestMethod.GET)
  public OrgUnit getOrgUnit(@PathVariable("id") String id) {
    log.debug("============Original==================");
    return orgUnitService.getOrgUnit(Long.valueOf(id));
  }

  @RequestMapping(value = "orgunit2/{id}", method = RequestMethod.GET)
  public OrgUnit getOrgUnitManipulated(@PathVariable("id") String id) {
    log.debug("============!!!Manipulated!!!==================");
    OrgUnit original = orgUnitService.getOrgUnit(Long.valueOf(id));
    log.debug("Original OrgUnit [{}]: {}", id, original);
    original.setName("Manipulated");
    OrgUnit manipulated = orgUnitService.getOrgUnit(Long.valueOf(id));
    orgUnitService.flush();
    return manipulated;
  }

  @RequestMapping(value = "orgunit/{id}", method = RequestMethod.PUT)
  public OrgUnit updateOrgUnit(@PathVariable("id") String id, @RequestBody OrgUnit toBeUpdated) {
    OrgUnit existing = orgUnitService.updateOrgUnit(Long.valueOf(id), toBeUpdated);
    if (existing == null) {
      throw new NotFoundException();
    }
    return existing;
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class NotFoundException extends RuntimeException {
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public static class BadDataExeception extends RuntimeException {
  }

}
