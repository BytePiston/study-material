package com.sap.newton.mtmongo;

import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexEnsurer {
    private MongoMappingContext mappingContext;
    private MongoPersistentEntityIndexResolver resolver;
    private MongoTemplate mongoTemplate;

    public MongoIndexEnsurer(MongoMappingContext mappingContext, MongoTemplate mongoTemplate) {
        this.mappingContext = mappingContext;
        this.mongoTemplate = mongoTemplate;
        this.resolver = new MongoPersistentEntityIndexResolver(mappingContext);

    }

    public void ensureIndicesForTenant(String tenantId) {
        mappingContext.getPersistentEntities().forEach(entity -> {
            if (entity.findAnnotation(Document.class) != null && entity.getCollection().startsWith(tenantId)) {
                IndexOperations indexOperations = mongoTemplate.indexOps(entity.getCollection());
                for (MongoPersistentEntityIndexResolver.IndexDefinitionHolder holder : resolver.resolveIndexForEntity(entity)) {
                    indexOperations.ensureIndex(holder.getIndexDefinition());
                }
            }
        });
    }
}
