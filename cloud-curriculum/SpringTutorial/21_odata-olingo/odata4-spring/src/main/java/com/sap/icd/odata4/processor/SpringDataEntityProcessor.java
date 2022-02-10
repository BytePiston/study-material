package com.sap.icd.odata4.processor;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

import com.sap.icd.odata4.edm.SpringDataEdmProvider;

public class SpringDataEntityProcessor extends SpringDataProcessor
        implements EntityProcessor {

    public SpringDataEntityProcessor(OData odata,
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
    public void readEntity(ODataRequest request, ODataResponse response,
            UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        EdmEntitySet edmEntitySet = readEntitySet(uriInfo);

        List<UriParameter> keyPredicates = readKeyPredicates(uriInfo);

        if (keyPredicates.size() > 1)
            throw new IllegalArgumentException(
                    "Key must not contain more than one predicate");
        Entity entity = readEntity(edmEntitySet, keyPredicates);

        InputStream serializedContent = serializeEntity(responseFormat,
                edmEntitySet, entity);

        writeResponse(response, responseFormat, serializedContent);
    }

    private Entity readEntity(EdmEntitySet edmEntitySet,
            List<UriParameter> keyPredicates) {
        UriParameter keyPredicate = keyPredicates.get(0);
        String keyText = keyPredicate.getText();

        Entity entity = runWithRepositoryInvoker(edmEntitySet,
                (repositoryInformation, invoker, persistentEntity) -> {
                    if (invoker.hasFindOneMethod()) {
                        Object o = invoker.invokeFindOne(keyText);
                        return buildEntity(edmEntitySet, persistentEntity, o);
                    }
                    return null;
                });
        return entity;
    }

    @Override
    public void createEntity(ODataRequest request, ODataResponse response,
            UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEntity(ODataRequest request, ODataResponse response,
            UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteEntity(ODataRequest request, ODataResponse response,
            UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        // TODO Auto-generated method stub

    }

}
