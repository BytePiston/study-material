package com.sap.newton.ssmongo;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends MongoRepository<Asset, ObjectId>, AssetRepositoryCustom {
    Collection<Asset> findByName(String name);
}
