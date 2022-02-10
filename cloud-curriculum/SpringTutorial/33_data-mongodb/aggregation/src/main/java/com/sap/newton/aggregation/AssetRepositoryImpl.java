package com.sap.newton.aggregation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

public class AssetRepositoryImpl implements AssetRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Collection<AssetView> findAllWithStateCount() {
        Aggregation aggregation = newAggregation(lookup("alert", "_id", "assetId", "alerts"), unwind("alerts"),
            project("name", "_id").andExpression("alerts.state").as("state")
                    .and(when(Criteria.where("alerts.state").is("error")).then(1).otherwise(0)).as("error")
                    .and(when(Criteria.where("alerts.state").is("info")).then(1).otherwise(0)).as("info")
                    .and(when(Criteria.where("alerts.state").is("warning")).then(1).otherwise(0)).as("warning"),
            group("_id", "state").first("_id").as("assetId").first("name").as("name").first("state").as("state")
                    .sum("error").as("errors").sum("info").as("infos").sum("warning").as("warnings"));
        AggregationResults<AssetView> result = mongoTemplate.aggregate(aggregation, "asset", AssetView.class);
        return result.getMappedResults();
    }

    @Override
    public AssetView findWithStateCount(ObjectId id) {
        Aggregation aggregation = newAggregation(match(Criteria.where("_id").is(id)), lookup("alert", "_id", "assetId", "alerts"), unwind("alerts"),
            project("name", "_id").andExpression("alerts.state").as("state")
                    .and(when(Criteria.where("alerts.state").is("error")).then(1).otherwise(0)).as("error")
                    .and(when(Criteria.where("alerts.state").is("info")).then(1).otherwise(0)).as("info")
                    .and(when(Criteria.where("alerts.state").is("warning")).then(1).otherwise(0)).as("warning"),
            group("_id", "state").first("_id").as("assetId").first("name").as("name").first("state").as("state")
                    .sum("error").as("errors").sum("info").as("infos").sum("warning").as("warnings"));
        AggregationResults<AssetView> result = mongoTemplate.aggregate(aggregation, "asset", AssetView.class);
        return result != null ? result.getUniqueMappedResult(): null;
    }

}
