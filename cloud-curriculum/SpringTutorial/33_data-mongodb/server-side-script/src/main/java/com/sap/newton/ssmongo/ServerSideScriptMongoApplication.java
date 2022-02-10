package com.sap.newton.ssmongo;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

@SpringBootApplication
public class ServerSideScriptMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerSideScriptMongoApplication.class, args);
    }
    
    @Bean
    @Profile("test")
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
}
