package com.sap.icd.odata4.processor;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

import com.sap.icd.odata4.edm.SpringDataEdmProvider;

public class SpringDataProcessor implements Processor {
    private final OData                    odata;
    private final ServiceMetadata          serviceMetadata;
    private final SpringDataEdmProvider    edmProvider;
    private final Repositories             repositories;
    private final PersistentEntities       persistentEntities;

    private final RepositoryInvokerFactory invokerFactory;
    @SuppressWarnings("unused")
    private final ProjectionFactory        projectionFactory;

    public SpringDataProcessor(OData odata, ServiceMetadata serviceMetadata,
            SpringDataEdmProvider edmProvider, Repositories repositories,
            PersistentEntities persistentEntities,
            RepositoryInvokerFactory invokerFactory,
            ProjectionFactory projectionFactory) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
        this.edmProvider = edmProvider;
        this.repositories = repositories;
        this.persistentEntities = persistentEntities;
        this.invokerFactory = invokerFactory;
        this.projectionFactory = projectionFactory;
    }

    protected interface RunWithRepositoryInvoker<T> {
        T run(RepositoryInformation repositoryInformation,
                RepositoryInvoker invoker,
                PersistentEntity<?, ?> persistentEntity);
    }

    protected <T> T runWithRepositoryInvoker(EdmEntitySet edmEntitySet,
            RunWithRepositoryInvoker<T> runner) {
        RepositoryInformation repositoryInformation = edmProvider
                .getRepositoryInformationFor(edmEntitySet.getName());
        Class<?> domainType = repositoryInformation.getDomainType();
        RepositoryInvoker invoker = invokerFactory.getInvokerFor(domainType);
        PersistentEntity<?, ?> persistentEntity = persistentEntities
                .getPersistentEntity(domainType);
        return runner.run(repositoryInformation, invoker, persistentEntity);
    }
    
    protected interface RunWithRepository<T> {
        T run(Object repository, Class<?> domainType);
    }
    
    protected <T> T runWithRepository(EdmEntitySet edmEntitySet, RunWithRepository<T> runner) {
        RepositoryInformation repositoryInformation = edmProvider
                .getRepositoryInformationFor(edmEntitySet.getName());
        Class<?> domainType = repositoryInformation.getDomainType();
        Object repository = repositories.getRepositoryFor(domainType);
        return runner.run(repository, domainType);
    }
    
    protected InputStream serializePrimitive(ContentType responseFormat,
            EdmEntitySet edmEntitySet, EdmProperty edmProperty, Object value) throws SerializerException {
        String edmPropertyName = edmProperty.getName();
        EdmPrimitiveType edmPropertyType = (EdmPrimitiveType) edmProperty.getType();

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).navOrPropertyPath(edmPropertyName).build();
        ODataSerializer serializer = odata.createSerializer(responseFormat);
        
        Property property = new Property();
        property.setName(edmPropertyName);
        property.setType(edmPropertyType.getName());
        property.setValue(ValueType.PRIMITIVE, value);
        PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextUrl).build();
        SerializerResult serializerResult = serializer.primitive(serviceMetadata, edmPropertyType, property, options);
        return serializerResult.getContent();
    }

    protected InputStream serializeEntity(ContentType responseFormat,
            EdmEntitySet edmEntitySet, Entity entity)
            throws SerializerException {
        EdmEntityType entityType = edmEntitySet.getEntityType();

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet)
                .build();
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        EntitySerializerOptions options = EntitySerializerOptions.with()
                .contextURL(contextUrl).build();

        SerializerResult serializerResult = serializer.entity(serviceMetadata,
                entityType, entity, options);
        InputStream serializedContent = serializerResult.getContent();
        return serializedContent;
    }

    protected InputStream serializeEntityCollection(ContentType responseFormat,
            EdmEntitySet edmEntitySet, EntityCollection entityCollection,
            ODataRequest request) throws SerializerException {
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet)
                .build();
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions
                .with().id(id).contextURL(contextUrl).build();

        SerializerResult serializerResult = serializer.entityCollection(
                serviceMetadata, edmEntityType, entityCollection, options);
        InputStream serializedContent = serializerResult.getContent();
        return serializedContent;
    }

    protected EdmEntitySet readEntitySet(UriInfo uriInfo) {
        EdmEntitySet edmEntitySet = readUriResourceEntitySet(uriInfo)
                .getEntitySet();
        return edmEntitySet;
    }

    protected List<UriParameter> readKeyPredicates(UriInfo uriInfo) {
        List<UriParameter> keyPredicates = readUriResourceEntitySet(uriInfo)
                .getKeyPredicates();
        return keyPredicates;
    }

    protected UriResourceEntitySet readUriResourceEntitySet(UriInfo uriInfo) {
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourceParts
                .get(0);
        return uriResourceEntitySet;
    } 
    
    protected EdmProperty readEdmProperty(UriInfo uriInfo) {
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        UriResourceProperty uriProperty = (UriResourceProperty) resourceParts.get(resourceParts.size() -1);
        EdmProperty edmProperty = uriProperty.getProperty();
        return edmProperty;
    }

    protected void writeResponse(ODataResponse response,
            ContentType responseFormat, InputStream serializedContent) {
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE,
                responseFormat.toContentTypeString());
    }
    
    protected Entity buildEntity(EdmEntitySet edmEntitySet,
            PersistentEntity<?, ?> persistentEntity, Object e) {
        Entity entity = new Entity();
        for (String propertyName : edmEntitySet.getEntityType()
                .getPropertyNames()) {
            Object o;
            o = persistentEntity.getPropertyAccessor(e).getProperty(
                    persistentEntity.getPersistentProperty(propertyName));
            entity.addProperty(
                    new Property(null, propertyName, ValueType.PRIMITIVE, o));
        }
        try {
            entity.setId(new URI(String.format("%s(%s)", edmEntitySet.getName(),
                    entity.getProperty(
                            persistentEntity.getIdProperty().getName())
                            .asPrimitive().toString())));
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        return entity;
    }

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        // TODO Auto-generated method stub
        
    }
    
    public PersistentEntities getPersistentEntities() {
        return persistentEntities;
    }
}