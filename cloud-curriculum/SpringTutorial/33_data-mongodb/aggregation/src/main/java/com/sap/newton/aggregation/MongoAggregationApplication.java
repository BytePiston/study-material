package com.sap.newton.aggregation;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

@SpringBootApplication
public class MongoAggregationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoAggregationApplication.class, args);
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, @Value("${random.name}") String randomName) throws Exception {
        String dbname = "db_" + randomName;
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, dbname) {
            @SuppressWarnings("unused")
            public void shutdown() {
                mongoClient.dropDatabase(dbname);
            }
        };
        return mongoTemplate; 
    }
    
    @Bean
    public CommandLineRunner initDB(AssetRepository assetRepository, AlertRepository alertRepository) {
        return (args) -> {
            assetRepository.save(Asset.builder().name("turbine").build());
            assetRepository.save(Asset.builder().name("wellhead").build());
            Asset asset = assetRepository.findByName("turbine");
            alertRepository.save(Alert.builder().assetId(asset.getId()).state("error").build());
            Collection<AssetView> assetViews = assetRepository.findAllWithStateCount();
            System.out.println(assetViews);
        };
    }
}
