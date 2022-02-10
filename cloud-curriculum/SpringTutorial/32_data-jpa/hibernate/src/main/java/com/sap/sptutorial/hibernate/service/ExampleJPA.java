package com.sap.sptutorial.hibernate.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.stereotype.Component;

import com.sap.sptutorial.hibernate.domain.Employee;
import com.sap.sptutorial.hibernate.domain.OrgUnit;

@Component
public class ExampleJPA {
    private OrgUnitRepository repo;

    public ExampleJPA(OrgUnitRepository repo) {
        this.repo = repo;
    }
    
    public void create() {
        OrgUnit unit = new OrgUnit(null);
        repo.save(unit);
    }
}
