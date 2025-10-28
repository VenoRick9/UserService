package by.baraznov.userservice.kafka;

import by.baraznov.userservice.dto.user.UserCreatedEvent;
import by.baraznov.userservice.dto.user.UserDeletedEvent;
import by.baraznov.userservice.dto.user.UserEvent;
import by.baraznov.userservice.dto.user.UserUpdatedEvent;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class OutBoxEventListener {

    private final UserQueryRepository userQueryRepository;

    @KafkaListener(topics = "USER_CREATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserCreateEvent(UserEvent message) {
        if (message instanceof UserCreatedEvent created) {
            handleUserCreated(created);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }

    @KafkaListener(topics = "USER_UPDATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserUpdateEvent(UserEvent message) {
        if (message instanceof UserUpdatedEvent updated) {
            handleUserUpdated(updated);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }
    @KafkaListener(topics = "USER_DELETED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserDeleteEvent(UserEvent message) {
        if (message instanceof UserDeletedEvent deleted) {
            handleUserDeleted(deleted);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }



    private void handleUserCreated(UserCreatedEvent e) {
        UserQuery userQuery = UserQuery.builder()
                .id(e.id().toString())
                .name(e.name())
                .surname(e.surname())
                .email(e.email())
                .birthDate(e.birthDate())
                .cards(new ArrayList<>())
                .build();
        userQueryRepository.save(userQuery);
    }

    private void handleUserUpdated(UserUpdatedEvent e) {
        userQueryRepository.findById(e.id().toString()).ifPresent(user -> {
            user.setName(e.name());
            user.setSurname(e.surname());
            user.setEmail(e.email());
            user.setBirthDate(e.birthDate());
            userQueryRepository.save(user);
        });
    }
    private void handleUserDeleted(UserDeletedEvent deleted) {
        userQueryRepository.deleteById(deleted.id().toString());
    }
}
