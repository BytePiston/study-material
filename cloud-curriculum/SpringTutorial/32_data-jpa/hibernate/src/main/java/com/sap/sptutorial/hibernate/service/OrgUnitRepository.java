package com.sap.sptutorial.hibernate.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sap.sptutorial.hibernate.domain.OrgUnit;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long>{

}
