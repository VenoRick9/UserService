package by.baraznov.userservice.controller;

import by.baraznov.userservice.config.TestContainersConfig;
import by.baraznov.userservice.model.CardInfo;
import by.baraznov.userservice.model.User;
import by.baraznov.userservice.repository.CardInfoRepository;
import by.baraznov.userservice.repository.UserRepository;
import by.baraznov.userservice.util.JwtUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestContainersConfig.class})
@Testcontainers
class CardInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CardInfoRepository cardInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtilTest testJwtUtil;

    private String token;

    private User user;
    private CardInfo card1, card2;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE card_info RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        UUID userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .name("Test")
                .surname("User")
                .email("test.user@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        userRepository.save(user);

        card1 = CardInfo.builder()
                .number("1234567890123456")
                .holder("TEST USER")
                .expirationDate(LocalDate.of(2030, 1, 1))
                .user(user)
                .build();

        card2 = CardInfo.builder()
                .number("1111222233334444")
                .holder("USER TEST")
                .expirationDate(LocalDate.of(2029, 12, 31))
                .user(user)
                .build();

        cardInfoRepository.saveAll(List.of(card1, card2));
        token = testJwtUtil.generateToken(user);
    }

    @Test
    public void test_getAllCards() throws Exception {
        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].number").value("1234567890123456"));
    }

    @Test
    public void test_getCardById() throws Exception {
        mockMvc.perform(get("/cards/{id}", card1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("TEST USER"));
    }

    @Test
    public void test_getCardsByIds() throws Exception {
        mockMvc.perform(get("/cards")
                        .param("ids", String.valueOf(card1.getId()), String.valueOf(card2.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].number").value("1234567890123456"));
    }

    @Test
    public void test_createCard() throws Exception {
        String json = """
                    {
                      "number": "9999888877776666",
                      "holder": "NEW HOLDER",
                      "expirationDate": "2031-12-12"
                    }
                """;

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("NEW HOLDER"))
                .andExpect(jsonPath("$.number").value("9999888877776666"));
    }

    @Test
    public void test_updateCard() throws Exception {
        String json = """
                    {
                      "number": "5555444433332222",
                      "holder": "UPDATED HOLDER",
                      "expirationDate": "2035-01-01"
                    }
                """;

        mockMvc.perform(patch("/cards/{id}", card1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("5555444433332222"))
                .andExpect(jsonPath("$.holder").value("UPDATED HOLDER"));
    }

    @Test
    public void test_deleteCard() throws Exception {
        mockMvc.perform(delete("/cards/{id}", card2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertFalse(cardInfoRepository.findById(card2.getId()).isPresent());
    }
}

