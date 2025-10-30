package by.baraznov.userservice.kafka;

import by.baraznov.userservice.dto.Event;
import by.baraznov.userservice.dto.card.CardCreatedEvent;
import by.baraznov.userservice.dto.card.CardDeletedEvent;
import by.baraznov.userservice.dto.card.CardUpdateEvent;
import by.baraznov.userservice.dto.user.UserCreatedEvent;
import by.baraznov.userservice.dto.user.UserDeletedEvent;
import by.baraznov.userservice.dto.user.UserUpdatedEvent;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.util.CardNotFound;
import by.baraznov.userservice.util.UserNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class OutBoxEventListener {

    private final UserQueryRepository userQueryRepository;

    @KafkaListener(topics = "USER_CREATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserCreateEvent(Event message) {
        if (message instanceof UserCreatedEvent created) {
            handleUserCreated(created);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }

    @KafkaListener(topics = "USER_UPDATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserUpdateEvent(Event message) {
        if (message instanceof UserUpdatedEvent updated) {
            handleUserUpdated(updated);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }
    @KafkaListener(topics = "USER_DELETED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onUserDeleteEvent(Event message) {
        if (message instanceof UserDeletedEvent deleted) {
            handleUserDeleted(deleted);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }
    @KafkaListener(topics = "CARD_CREATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onCardCreateEvent(Event message) {
        if (message instanceof CardCreatedEvent created) {
            handleCardCreated(created);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }
    @KafkaListener(topics = "CARD_UPDATED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onCardUpdateEvent(Event message) {
        if (message instanceof CardUpdateEvent updated) {
            handleCardUpdated(updated);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }
    @KafkaListener(topics = "CARD_DELETED_TOPIC", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
    public void onCardDeletedEvent(Event message) {
        if (message instanceof CardDeletedEvent deleted) {
            handleCardDeleted(deleted);
        }  else {
            throw new IllegalArgumentException("Unknown event type: " + message.getClass());
        }
    }

    private void handleCardDeleted(CardDeletedEvent deleted) {
        UserQuery user = userQueryRepository.findByCards_Id(deleted.cardId())
                .orElseThrow(() -> new UserNotFound(
                        "User not found for card with id " + deleted.cardId()
                ));
        boolean removed = user.getCards().removeIf(card -> card.getId().equals(deleted.cardId()));
        if (!removed) {
            throw new CardNotFound("Card with id " + deleted.cardId() + " not found for user " + user.getId());
        }
        userQueryRepository.save(user);
    }


    private void handleCardUpdated(CardUpdateEvent updated) {
        UserQuery user = userQueryRepository.findById(String.valueOf(updated.id()))
                .orElseThrow(()-> new UserNotFound("User not found"));
        CardInfoQuery card = user.getCards().stream()
                .filter(c -> c.getId().equals(updated.cardId()))
                .findFirst()
                .orElseThrow(() -> new CardNotFound("Card with id " + updated.cardId() + " not found"));
        if (updated.number() != null) card.setNumber(updated.number());
        if (updated.holder() != null) card.setHolder(updated.holder());
        if (updated.expirationDate() != null) card.setExpirationDate(updated.expirationDate());
        userQueryRepository.save(user);
    }

    private void handleCardCreated(CardCreatedEvent created) {
        UserQuery user = userQueryRepository.findById(String.valueOf(created.id()))
                .orElseThrow(()-> new UserNotFound("User not found"));
        user.addCard(new CardInfoQuery(created.cardId(), String.valueOf(created.id()), created.number(),
                created.holder(), created.expirationDate()));
        userQueryRepository.save(user);
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
