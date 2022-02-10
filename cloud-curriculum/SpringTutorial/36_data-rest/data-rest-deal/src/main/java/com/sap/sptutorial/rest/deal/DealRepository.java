package com.sap.sptutorial.rest.deal;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource(exported = true, path = "deals", collectionResourceRel = "deals")
public interface DealRepository extends PagingAndSortingRepository<Deal, Long> {

    Collection<Deal> findByTitle(@Param("title") String title);
    
    Collection<Deal> findByFeatured(@Param("featured") Boolean featured);
    
    @Query("select d from Deal d where lower(d.title) like %:title")
    Collection<Deal> findByTitleEndsWith(@Param("title") String title);
}
