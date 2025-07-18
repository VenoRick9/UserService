package by.baraznov.userservice;

import by.baraznov.userservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Import(TestContainersConfig.class)
@Testcontainers

class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
