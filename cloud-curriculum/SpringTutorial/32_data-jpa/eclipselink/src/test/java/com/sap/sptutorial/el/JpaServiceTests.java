package com.sap.sptutorial.el;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sap.sptutorial.el.domain.Employee;
import com.sap.sptutorial.el.domain.OrgUnit;
import com.sap.sptutorial.el.service.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
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
