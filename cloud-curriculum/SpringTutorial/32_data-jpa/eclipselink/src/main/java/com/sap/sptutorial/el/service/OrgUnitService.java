package com.sap.sptutorial.el.service;

import com.sap.sptutorial.el.domain.OrgUnit;
import com.sap.sptutorial.el.web.JpaRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Slf4j
@Component
public class OrgUnitService {
  @PersistenceContext(type = PersistenceContextType.TRANSACTION)
  private EntityManager em;

  public OrgUnit getOrgUnit(Long id) {
    OrgUnit found = em.find(OrgUnit.class, id);
    //em.detach(found);
    return found;
  }

  @Transactional
  public void flush() {
    em.flush();
  }

  @Transactional
  public OrgUnit updateOrgUnit(Long id, OrgUnit toBeUpdated) {
    String newOrgName = toBeUpdated.getName();
    if (newOrgName == null || newOrgName.isEmpty()) {
      throw new JpaRestController.BadDataExeception();
    }
    OrgUnit existing = em.find(OrgUnit.class, id);
    if (existing == null) {
      throw new JpaRestController.NotFoundException();
    }

    existing.setName(newOrgName);

    return existing;
  }
}
