package com.sap.sptutorial.openjpa;

import com.sap.sptutorial.openjpa.JpaApplication;
import com.sap.sptutorial.openjpa.domain.Employee;
import com.sap.sptutorial.openjpa.domain.OrgUnit;
import com.sap.sptutorial.openjpa.service.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.persistence.EntityManager;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(JpaApplication.class)
public class JpaServiceTests {

  @Autowired
  private EmployeeRepository repo;

  @Autowired
  private EntityManager em;

  @Test
  public void getEmployeeById() {
    Employee employee = repo.findOne(Long.valueOf(1));
    Assert.assertNotNull(employee);
    Assert.assertEquals(Long.valueOf(1), employee.getId());
  }

  @Test
  public void getOrgUnitById() {
    OrgUnit ou = em.find(OrgUnit.class, Long.valueOf(1));
    Assert.assertNotNull(ou);
    Assert.assertEquals(Long.valueOf(1), ou.getId());
  }
}
