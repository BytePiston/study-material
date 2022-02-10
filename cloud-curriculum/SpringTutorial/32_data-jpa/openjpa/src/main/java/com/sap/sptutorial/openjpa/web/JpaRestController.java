package com.sap.sptutorial.openjpa.web;

import com.sap.sptutorial.openjpa.domain.Employee;
import com.sap.sptutorial.openjpa.domain.OrgUnit;
import com.sap.sptutorial.openjpa.service.EmployeeRepository;
import com.sap.sptutorial.openjpa.service.OrgUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@Slf4j
@RestController
public class JpaRestController {

  @Autowired
  private OrgUnitService orgUnitService;

  @Autowired
  private EmployeeRepository employeeRepository;

  @RequestMapping(value = "employee/{id}", method = RequestMethod.GET)
  public Employee getEmployee(@PathVariable("id") String id) {
    Employee employee = employeeRepository.findOne(Long.valueOf(id));
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
