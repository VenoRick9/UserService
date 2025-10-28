package by.baraznov.userservice.dto.user;

import by.baraznov.userservice.dto.card.CardCreatedEvent;
import by.baraznov.userservice.dto.card.CardDeletedEvent;
import by.baraznov.userservice.dto.card.CardUpdateEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED"),
        @JsonSubTypes.Type(value = UserUpdatedEvent.class, name = "USER_UPDATED"),
        @JsonSubTypes.Type(value = UserDeletedEvent.class, name = "USER_DELETED"),
        @JsonSubTypes.Type(value = CardCreatedEvent.class, name = "CARD_CREATED"),
        @JsonSubTypes.Type(value = CardUpdateEvent.class, name = "CARD_UPDATED"),
        @JsonSubTypes.Type(value = CardDeletedEvent.class, name = "CARD_DELETED")
})
public interface Event {
    UUID id();
    String eventType();
}