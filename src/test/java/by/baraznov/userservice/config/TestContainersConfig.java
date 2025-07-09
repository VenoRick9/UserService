package by.baraznov.userservice.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class TestContainersConfig {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("user-service")
            .withUsername("postgres")
            .withPassword("Postgres_9");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.4.2")
            .withExposedPorts(6379);

    static {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        redisContainer.start();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", String.valueOf(redisContainer.getMappedPort(6379)));

    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
