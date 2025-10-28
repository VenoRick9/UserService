package by.baraznov.userservice.kafka;

import by.baraznov.userservice.dto.user.UserCreatedEvent;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class OutBoxEventListener {

    private final ObjectMapper objectMapper;
    private final UserQueryRepository userQueryRepository;

    @KafkaListener(topics = "CREATE_USER", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserEvent(UserCreatedEvent message) {
        try {
            UserQuery userQuery = UserQuery.builder()
                    .id(String.valueOf(message.id()))
                    .name(message.name())
                    .surname(message.surname())
                    .email(message.email())
                    .birthDate(message.birthDate())
                    .cards(new ArrayList<>())
                    .build();
            userQueryRepository.save(userQuery);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing user event", e);
        }
    }
}
