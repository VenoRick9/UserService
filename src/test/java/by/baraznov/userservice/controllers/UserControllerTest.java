package by.baraznov.userservice.controllers;

import by.baraznov.userservice.config.TestContainersConfig;
import by.baraznov.userservice.models.User;
import by.baraznov.userservice.repositories.UserRepository;
import by.baraznov.userservice.util.JwtUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@Testcontainers
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtUtilTest testJwtUtil;


    private User user1, user2;
    private String token;


    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
        user1 = User.builder()
                .id(1)
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .cards(List.of())
                .build();

        user2 = User.builder()
                .id(2)
                .name("Alice")
                .surname("Smith")
                .birthDate(LocalDate.of(1985, 5, 20))
                .email("alice.smith@example.com")
                .cards(List.of())
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        token = testJwtUtil.generateToken(user1);
    }


    @Test
    public void test_getAllUsers() throws Exception {

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")

                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John"))
                .andExpect(jsonPath("$.content[1].surname").value("Smith"))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.content[0].birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.content[0].cards").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)));
        String cacheKey = "allUsers::Page request [number: 0, size 10, sort: id: ASC]";
        assertNotNull(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)));
        assertTrue(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)).contains("John"));

    }

    @Test
    public void test_getUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", user1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
        String cacheKey = "user::1";
        assertNotNull(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)));
        assertTrue(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)).contains("John"));
    }

    @Test
    public void test_getUsersByIds() throws Exception {
        mockMvc.perform(get("/users")
                        .param("ids", String.valueOf(user1.getId()), String.valueOf(user2.getId()))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[1].email", is("alice.smith@example.com")));
    }

    @Test
    public void test_getUserByEmail() throws Exception {
        mockMvc.perform(get("/users")
                        .param("email", String.valueOf(user1.getEmail()))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")));
        String cacheKey = "user::john.doe@example.com";
        assertNotNull(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)));
        assertTrue(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)).contains("John"));
    }

    @Test
    public void test_createUser() throws Exception {
        token = testJwtUtil.generateToken(new User()
                .builder().id(3).build());
        String json = """
                    {
                      "name": "Bob",
                      "surname": "Brown",
                      "birthDate": "1995-05-05",
                      "email": "bob.brown@example.com"
                    }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob.brown@example.com"));

        assertEquals(3, userRepository.count());
        String cacheKey = "user::3";
        assertNotNull(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)));
        assertTrue(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)).contains("Bob"));
    }

    @Test
    public void test_updateUser() throws Exception {
        String json = """
                    {
                      "name": "Johnny",
                      "surname": "Doe",
                      "birthDate": "1990-01-01",
                      "email": "johnny.doe@example.com"
                    }
                """;

        mockMvc.perform(patch("/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Johnny"))
                .andExpect(jsonPath("$.email").value("johnny.doe@example.com"));
        String cacheKey = "user::1";
        assertNotNull(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)));
        assertTrue(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(cacheKey)).contains("Johnny"));
    }

    @Test
    public void test_deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", user2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        assertEquals(1, userRepository.count());
        String cacheKey = "user::2";
        assertNull(stringRedisTemplate.opsForValue().get(cacheKey));
    }
}