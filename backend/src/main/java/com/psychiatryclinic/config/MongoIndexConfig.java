package com.psychiatryclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MongoIndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        try {
            // User collection indexes
            mongoTemplate.indexOps("users").ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());

            // Doctor collection indexes
            mongoTemplate.indexOps("doctors").ensureIndex(new Index().on("licenseNumber", Sort.Direction.ASC).unique());

            // Appointment collection indexes
            mongoTemplate.indexOps("appointments").ensureIndex(new Index().on("appointmentDateTime", Sort.Direction.ASC));
            mongoTemplate.indexOps("appointments").ensureIndex(new Index().on("doctor", Sort.Direction.ASC));
            mongoTemplate.indexOps("appointments").ensureIndex(new Index().on("patient", Sort.Direction.ASC));

            // Treatment collection indexes
            mongoTemplate.indexOps("treatments").ensureIndex(new Index().on("doctor", Sort.Direction.ASC));
            mongoTemplate.indexOps("treatments").ensureIndex(new Index().on("patient", Sort.Direction.ASC));
            
            log.info("MongoDB indexes created successfully");
        } catch (Exception e) {
            log.error("Error creating MongoDB indexes: " + e.getMessage(), e);
        }
    }
} 