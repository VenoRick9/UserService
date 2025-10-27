package by.baraznov.userservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"by.baraznov.userservice.write.repository",
        "by.baraznov.userservice.repository"})
@EntityScan(basePackages = {"by.baraznov.userservice.write.model",
        "by.baraznov.userservice.model"})
public class PostgresConfig {
}