package com.sap.icd.odata4.processor;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.SingletonPrimitiveType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.util.ReflectionUtils;

import com.sap.icd.odata4.edm.EdmTypeConverter;
import com.sap.icd.odata4.edm.SpringDataEdmProvider;
import com.sap.icd.odata4.repo.ODataRepositoryExtension;

public class SpringDataPrimitiveProcessor extends SpringDataProcessor
        implements PrimitiveProcessor {

    public SpringDataPrimitiveProcessor(OData odata,
            ServiceMetadata serviceMetadata, SpringDataEdmProvider edmProvider,
            Repositories repositories, PersistentEntities persistentEntities,
            RepositoryInvokerFactory invokerFactory,
            ProjectionFactory projectionFactory) {
        super(odata, serviceMetadata, edmProvider, repositories,
                persistentEntities, invokerFactory, projectionFactory);
    }

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
    }

    @Override
    public void readPrimitive(ODataRequest request, ODataResponse response,
            UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        EdmEntitySet entitySet = readEntitySet(uriInfo);
        List<UriParameter> keyPredicates = readKeyPredicates(uriInfo);
        List<UriResource> resourceParts = uriInfo.getUriResourceParts();
        UriResourceProperty uriProperty = (UriResourceProperty) resourceParts
                .get(resourceParts.size() - 1);
        EdmProperty edmProperty = uriProperty.getProperty();

        Object value = readEntityField(entitySet, keyPredicates, edmProperty);
        InputStream serializedContent = serializePrimitive(responseFormat,
                entitySet, edmProperty, value);
        writeResponse(response, responseFormat, serializedContent);
    }

    private Object readEntityField(EdmEntitySet edmEntitySet,
            List<UriParameter> keyPredicates, EdmProperty edmProperty) {
        UriParameter keyPredicate = keyPredicates.get(0);
        String keyValue = keyPredicate.getText();
        String keyName = keyPredicate.getName();
        String edmPropertyName = edmProperty.getName();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        SingletonPrimitiveType edmKeyPropertyType = (SingletonPrimitiveType) edmEntityType.getProperty(keyName).getType();
        Object value = runWithRepository(edmEntitySet,
                (repository, domainType) -> {
                    try {
                        Method method = ReflectionUtils
                                .findMethod(ODataRepositoryExtension.class,
                                        "findSingleField",
                                        new Class<?>[] { PersistentEntity.class,
                                                String.class,
                                                Serializable.class });
                        InvocationHandler handler = Proxy
                                .getInvocationHandler(repository);
                        Object[] parameters = new Object[] {
                                getPersistentEntities()
                                        .getPersistentEntity(domainType),
                                edmPropertyName, EdmTypeConverter.deserialize(edmKeyPropertyType, keyValue) };
                        Object v = handler.invoke(repository, method,
                                parameters);
                        return v;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        return null;
                    }
                });
        return value;
    }

    @Override
    public void updatePrimitive(ODataRequest request, ODataResponse response,
            UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePrimitive(ODataRequest request, ODataResponse response,
            UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        // TODO Auto-generated method stub

    }

}
