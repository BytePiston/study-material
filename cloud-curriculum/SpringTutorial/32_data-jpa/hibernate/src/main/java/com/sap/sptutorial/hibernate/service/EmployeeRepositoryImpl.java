package com.sap.sptutorial.hibernate.service;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sap.sptutorial.hibernate.domain.Employee;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeRepositoryImpl implements EmployeeCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<Employee> findEmployeesWorkingAt(String department) {
        log.debug("findEmployeesWorkingAt({})", department);
        final String sql = "select e from Employee e join e.orgUnit o where upper(o.name) = upper(:dept)";
        return em.createQuery(sql, Employee.class)
                .setParameter("dept", department).getResultList();
    }
}
