/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stored.procedures.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stored.procedures.domain.Employee;
import stored.procedures.domain.OrgUnit;
import stored.procedures.service.OrgUnitRepository;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

@Slf4j
@Controller
public class SampleController {

  @Autowired
  private EntityManager em;

  @Autowired
  private OrgUnitRepository orgUnitRepo;

  @RequestMapping("/")
  @ResponseBody
  public String helloWorld() {
    return "Hello";
  }

  @RequestMapping("employee/{id}")
  @ResponseBody
  public String getEmployee(@PathVariable("id") String id) {
    Employee employee = em.find(Employee.class, Long.valueOf(id));
    if (employee == null)
      return String.format("No employee found with the ID: %s", id);

    return String.format("id=%d, name=%s %s", employee.getId(), employee.getFirstName(), employee.getLastName());
  }

  @RequestMapping("orgunit/{id}")
  @ResponseBody
  public String getOrgUnit(@PathVariable("id") String id) {
    OrgUnit ou = orgUnitRepo.findOne(Long.valueOf(id));
    if (ou == null) {
      return String.format("No OrgUnit found with the ID: %s", id);
    }
    return String.format("id=%d, name=%s", ou.getId(), ou.getName());
  }

  @RequestMapping(value = "rand", method = RequestMethod.GET)
  @ResponseBody
  public Double random() {
    // Dynamically create a stored procedure query
    log.debug("Executing ....... random()");
    StoredProcedureQuery q = em.createStoredProcedureQuery("JAVA_RANDOM");
    return (Double) q.getSingleResult();
  }

  @RequestMapping(value = "power", method = RequestMethod.GET)
  @ResponseBody
  public Double power(@RequestParam("base") Double base, @RequestParam("exponent") Double exponent) {
    // Create a stored procedure out of a named query
    log.debug("Executing .......power({}, {})", base, exponent);
    StoredProcedureQuery q = em.createNamedStoredProcedureQuery("POWER");
    q.setParameter("base", base);
    q.setParameter("exponent", exponent);
    return (Double) q.getSingleResult();
  }
}
