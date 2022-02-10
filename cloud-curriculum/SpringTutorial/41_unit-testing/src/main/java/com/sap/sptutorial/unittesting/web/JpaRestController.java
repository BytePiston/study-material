package com.sap.sptutorial.unittesting.web;

import com.sap.sptutorial.unittesting.domain.Employee;
import com.sap.sptutorial.unittesting.domain.OrgUnit;
import com.sap.sptutorial.unittesting.service.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(value = "api")
public class JpaRestController {

  @Autowired
  private EntityManager em;

  @Autowired
  private EmployeeRepository emplSrvc;

  @RequestMapping(value = "employee/{id}", method = RequestMethod.GET)
  public Employee getEmployee(@PathVariable("id") String id) {
    Employee employee = emplSrvc.findOne(Long.valueOf(id));
    if (employee == null) {
      throw new NotFoundException();
    }
    return employee;
  }

  @RequestMapping(value = "employee", method = RequestMethod.GET)
  public Employee getEmployeeByEmail(@RequestParam("email") String email) {
    Employee employee = emplSrvc.findEmployeeByEmail(email);
    if (employee == null) {
      throw new NotFoundException();
    }
    return employee;
  }

  @RequestMapping(value = "employees", method = RequestMethod.GET)
  public Collection<Employee> getEmployees(@RequestParam("domain") String domain) {
    return emplSrvc.findByEmailLike("%" + domain);
  }

  @Transactional
  @RequestMapping(value = "employee", method = RequestMethod.POST)
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    Employee ret = emplSrvc.save(employee);
    return new ResponseEntity(ret, HttpStatus.OK);
  }

  @RequestMapping(value = "orgunit/{id}", method = RequestMethod.GET)
  public OrgUnit getOrgUnit(@PathVariable("id") String id) {
    OrgUnit ou = em.find(OrgUnit.class, Long.valueOf(id));
    if (ou == null) {
      throw new NotFoundException();
    }
    return ou;
  }

  @Transactional
  @RequestMapping(value = "orgunit/{id}", method = RequestMethod.PUT)
  public OrgUnit updateOrgUnit(@PathVariable("id") String id, @RequestBody OrgUnit updatedOrgUnit) {

    updatedOrgUnit.setId(Long.valueOf(id));
    OrgUnit ou = em.merge(updatedOrgUnit);

    if (ou == null) {
      throw new NotFoundException();
    }
    return ou;
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  static class NotFoundException extends RuntimeException {
  }
}
