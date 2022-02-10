package com.sap.icd.odata4.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RestResource;

@NoRepositoryBean
public interface ODataRepositoryExtension<T, ID extends Serializable>
        extends JpaRepository<T, ID> {
    @RestResource(path = "names")
    public Object findSingleField(PersistentEntity<?, ?> persistentEntity,
            String fieldName, ID id);
}
