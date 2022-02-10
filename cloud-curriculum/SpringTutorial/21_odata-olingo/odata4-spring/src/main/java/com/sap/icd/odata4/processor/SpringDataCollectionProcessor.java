package com.sap.icd.odata4.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

import com.sap.icd.odata4.edm.SpringDataEdmProvider;

public class SpringDataCollectionProcessor extends SpringDataProcessor
        implements EntityCollectionProcessor {

    public SpringDataCollectionProcessor(OData odata,
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
    public void readEntityCollection(ODataRequest request,
            ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        EdmEntitySet edmEntitySet = readEntitySet(uriInfo);

        EntityCollection entityCollection = getEntityCollection(edmEntitySet);

        InputStream serializedContent = serializeEntityCollection(
                responseFormat, edmEntitySet, entityCollection, request);

        // Finally: configure the response object: set the body, headers and
        // status code
        writeResponse(response, responseFormat, serializedContent);
    }

    private EntityCollection getEntityCollection(EdmEntitySet edmEntitySet) {
        EntityCollection entityCollection = new EntityCollection();

        List<Entity> entityList = runWithRepositoryInvoker(edmEntitySet,
                (repositoryInformation, invoker, persistentEntity) -> {
                    List<Entity> el = new ArrayList<>();
                    if (invoker.hasFindAllMethod()) {
                        Iterable<?> result;
                        Pageable pageable = repositoryInformation
                                .isPagingRepository() ? null : null;
                        result = invoker.invokeFindAll(pageable);
                        for (Object e : result) {
                            Entity entity = buildEntity(edmEntitySet,
                                    persistentEntity, e);
                            el.add(entity);
                        }
                    }
                    return el;
                });
        entityCollection.getEntities().addAll(entityList);
        return entityCollection;
    }
}
