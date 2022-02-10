package com.sap.newton.ssmongo;


import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends MongoRepository<Metric, ObjectId>{
    Collection<Metric> findByName(String name);
}
