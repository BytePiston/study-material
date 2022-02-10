package com.sap.newton.aggregation;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends MongoRepository<Asset, ObjectId>, AssetRepositoryCustom {
    Asset findByName(@Param("name") String name);
}
