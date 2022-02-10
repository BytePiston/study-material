package com.sap.sptutorial.hibernate.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.hibernate.domain.OrgUnit;
import com.sap.sptutorial.hibernate.service.OrgUnitRepository;

@RestController
@RequestMapping("orgunit")
public class OrgUnitController {
    private OrgUnitRepository orgUnitRepository;

    public OrgUnitController(OrgUnitRepository orgUnitRepository) {
        this.orgUnitRepository = orgUnitRepository;
    }

    @GetMapping
    public List<OrgUnit> orgunits() {
        return orgUnitRepository.findAll();
    }
    
}
