package com.sap.newton.ssmongo;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.NamedMongoScript;

public class AssetRepositoryImpl implements AssetRepositoryCustom {
    private static final String ASSOCIATE_METRICS_TO_ASSETS_NAME = "associateMetricsToAssets";

    private static final String ASSOCIATE_METRICS_TO_ASSETS =
    //@formatter:off
            String.join("\n", "function(assetName, metricNames) {",
                "assetCursor = db.asset.find({",
                    "\"name\": assetName",
                "});",
                "while (assetCursor.hasNext()) {",
                    "asset = assetCursor.next();",
                    "if (!asset.metrics) {",
                        "asset.metrics = [];",
                    "}",
                    "metricCursor = db.metric.find({",
                        "\"name\": {",
                            "$in: metricNames",
                        "}",
                    "});",
                    "while (metricCursor.hasNext()) {",
                        "metric = metricCursor.next();",
                        "asset.metrics.push(metric._id);",
                    "}",
                    "db.asset.save(asset);",
                "}",
            "}");
    //@formatter:on

    private ScriptOperations scriptOps;

    public AssetRepositoryImpl(MongoTemplate mongoTemplate) {
        this.scriptOps = mongoTemplate.scriptOps();
    }

    @PostConstruct
    public void init() {
        scriptOps.register(new NamedMongoScript(ASSOCIATE_METRICS_TO_ASSETS_NAME, ASSOCIATE_METRICS_TO_ASSETS));
    }

    @Override
    public void addMetrics(String assetName, Collection<String> metricNames) {
        scriptOps.call(ASSOCIATE_METRICS_TO_ASSETS_NAME, assetName, metricNames.toArray());
    }

}
