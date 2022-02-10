package com.sap.icd.odata4.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;

public class SpringDataEdmProvider extends CsdlAbstractEdmProvider
        implements InitializingBean {
    private String namespaceName = "Spring.OData";
    private String containerName = "Container";

    private final PersistentEntities                     persistentEntities;
    private final Repositories                           repositories;
    private final ResourceMappings                       resourceMappings;
    private final List<CsdlSchema>                       schemaList;
    private final CsdlEntityContainer                    entityContainer;
    private final MultiKeyMap                            entityTypeMap;
    private final MultiKeyMap                            entitySetMap;
    private final HashMap<String, RepositoryInformation> repositoryInformationMap;
    private final EdmTypeConverter                       edmTypeConverter;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public SpringDataEdmProvider(EdmTypeConverter edmTypeConverter,
            Repositories repositories, PersistentEntities persistentEntities,
            ResourceMappings resourceMappings) {
        this.repositories = repositories;
        this.persistentEntities = persistentEntities;
        this.resourceMappings = resourceMappings;
        this.schemaList = new ArrayList<CsdlSchema>();
        this.entityContainer = new CsdlEntityContainer();
        this.entityTypeMap = new MultiKeyMap();
        this.entitySetMap = new MultiKeyMap();
        this.repositoryInformationMap = new HashMap<>();
        this.edmTypeConverter = edmTypeConverter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (ResourceMetadata resourceMetadata : resourceMappings) {
            if (!resourceMetadata.isExported())
                continue;
            Class<?> domainType = resourceMetadata.getDomainType();
            String entitySetName = resourceMetadata.getRel();
            String entityName = resourceMetadata.getItemResourceRel();
            FullQualifiedName entityQName = new FullQualifiedName(
                    getNamespaceName(), entityName);

            // Entity Type
            PersistentEntity<?, ?> persistentEntity = persistentEntities
                    .getPersistentEntity(domainType);
            entityTypeMap.put(getNamespaceName(), entityName, getEntityType(
                    resourceMetadata, persistentEntity, entityQName));

            // Entity Set
            CsdlEntitySet entitySet = new CsdlEntitySet();
            entitySet.setName(entitySetName);
            entitySet.setType(entityQName);
            entitySetMap.put(getNamespaceName(), getContainerName(),
                    entitySetName, entitySet);

            // RepositoryInformation
            repositoryInformationMap.put(entitySetName,
                    repositories.getRepositoryInformationFor(domainType));
        }

        // Container
        @SuppressWarnings("unchecked")
        List<CsdlEntitySet> entitySetList = (List<CsdlEntitySet>) entitySetMap
                .entrySet().stream().filter(e -> {
                    Map.Entry<MultiKey, CsdlEntitySet> entry = (Map.Entry<MultiKey, CsdlEntitySet>) e;
                    return entry.getKey().getKey(0).equals(getNamespaceName())
                            && entry.getKey().getKey(1)
                                    .equals(getContainerName());
                }).map(e -> {
                    Map.Entry<MultiKey, CsdlEntitySet> entry = (Map.Entry<MultiKey, CsdlEntitySet>) e;
                    return entry.getValue();
                }).collect(Collectors.toList());
        entityContainer.setName(getContainerName());
        entityContainer.setEntitySets(entitySetList);

        // Schemas
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(getNamespaceName());
        @SuppressWarnings("unchecked")
        List<CsdlEntityType> entityTypes = (List<CsdlEntityType>) entityTypeMap
                .entrySet().stream().filter(e -> {
                    Map.Entry<MultiKey, CsdlEntitySet> entry = (Map.Entry<MultiKey, CsdlEntitySet>) e;
                    return entry.getKey().getKey(0).equals(getNamespaceName());
                }).map(e -> {
                    Map.Entry<MultiKey, CsdlEntitySet> entry = (Map.Entry<MultiKey, CsdlEntitySet>) e;
                    return entry.getValue();
                }).collect(Collectors.toList());
        schema.setEntityTypes(entityTypes);
        schema.setEntityContainer(entityContainer);
        schemaList.add(schema);
    }

    @Override
    public List<CsdlSchema> getSchemas() {
        return schemaList;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        return entityContainer;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(
            FullQualifiedName entityContainerName) throws ODataException {
        if (entityContainerName == null
                || entityContainerName.equals(getContainerName())) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(new FullQualifiedName(
                    getNamespaceName(), getContainerName()));
            return entityContainerInfo;
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer,
            String entitySetName) {
        return (CsdlEntitySet) entitySetMap.get(entityContainer.getNamespace(),
                entityContainer.getName(), entitySetName);
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName fqnEntity) {
        return (CsdlEntityType) entityTypeMap.get(fqnEntity.getNamespace(),
                fqnEntity.getName());
    }

    public RepositoryInformation getRepositoryInformationFor(
            String entitySetName) {
        return repositoryInformationMap.get(entitySetName);
    }

    private CsdlEntityType getEntityType(
            final ResourceMetadata resourceMetadata,
            final PersistentEntity<?, ?> persistentEntity,
            final FullQualifiedName entityQName) {
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(entityQName.getName());
        List<CsdlProperty> csdlProperties = new ArrayList<>();
        persistentEntity
                .doWithProperties((SimplePropertyHandler) propertyHandler -> {
                    CsdlProperty csdlProperty = new CsdlProperty();
                    csdlProperty.setName(propertyHandler.getName());
                    csdlProperty.setType(getEdmTypeForProperty(propertyHandler)
                            .getFullQualifiedName());
                    csdlProperties.add(csdlProperty);
                });
        entityType.setProperties(csdlProperties);
        PersistentProperty<?> idProperty = persistentEntity.getIdProperty();
        CsdlPropertyRef csdlIdPropertyRef = new CsdlPropertyRef();
        csdlIdPropertyRef.setName(idProperty.getName());
        entityType.setKey(Collections.singletonList(csdlIdPropertyRef));
        return entityType;
    }

    private EdmPrimitiveTypeKind getEdmTypeForProperty(
            PersistentProperty<?> property) {
        Class<?> propertyClass = property.getRawType();
        return edmTypeConverter.getEdmPrimitiveTypeFor(propertyClass);
    }
}
