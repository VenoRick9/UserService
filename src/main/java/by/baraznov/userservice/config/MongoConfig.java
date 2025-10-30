package by.baraznov.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "by.baraznov.userservice.read.repository")
public class MongoConfig {
}