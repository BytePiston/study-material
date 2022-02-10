package com.sap.sptutorial.validation;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends  JpaRepository<Deal, String>{
	Collection<Deal> findByTitleIgnoreCase(String title);
	Collection<Deal> findByTitleAndDescription(String title, String description);
}
