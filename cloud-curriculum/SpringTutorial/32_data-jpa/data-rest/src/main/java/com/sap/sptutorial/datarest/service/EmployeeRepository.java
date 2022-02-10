package com.sap.sptutorial.datarest.service;

import com.sap.sptutorial.datarest.domain.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Collection;

@RepositoryRestResource(collectionResourceRel = "colleagues", path = "colleagues")
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
  Employee findEmployeeByEmail(@Param("email") String email);

  Collection<Employee> findByEmailLike(@Param("domain") String domain);
}
