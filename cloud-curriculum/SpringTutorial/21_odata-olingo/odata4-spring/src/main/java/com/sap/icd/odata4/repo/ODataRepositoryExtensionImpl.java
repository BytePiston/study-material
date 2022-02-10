package com.sap.icd.odata4.repo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;

public class ODataRepositoryExtensionImpl<T, ID extends Serializable> extends
        SimpleJpaRepository<T, ID> implements ODataRepositoryExtension<T, ID> {

    private final EntityManager               entityManager;
    @SuppressWarnings("unused")
    private final JpaEntityInformation<T, ID> entityInformation;

    public ODataRepositoryExtensionImpl(
            JpaEntityInformation<T, ID> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityInformation = entityInformation;
        this.entityManager = em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object findSingleField(PersistentEntity<?, ?> persistentEntity,
            String fieldName, ID id) {
        PersistentProperty<?> property = persistentEntity
                .getPersistentProperty(fieldName);
        if (property != null) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            Class<?> entityType = persistentEntity.getType();
            Class<?> fieldType = property.getType();
            PersistentProperty<?> idProperty = persistentEntity.getIdProperty();
            Class<?> idFieldType = idProperty.getType();
            ParameterExpression<ID> parameter = (ParameterExpression<ID>) cb
                    .parameter(idFieldType);
            String idName = persistentEntity.getIdProperty().getName();
            CriteriaQuery<?> query = cb.createQuery(fieldType);
            Root<?> root = query.from(entityType);
            query = query.select(root.get(fieldName))
                    .where(cb.equal(root.get(idName), parameter));
            List<?> result = entityManager.createQuery(query)
                    .setParameter(parameter, id).getResultList();
            if (!result.isEmpty() && !(result.size() > 1))
                return result.get(0);
        }
        return null;
    }
}
