package by.baraznov.userservice.dto.card;

import by.baraznov.userservice.dto.user.Event;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardDeletedEvent(
        UUID id,
        Integer cardId
) implements Event {
    @Override
    public String eventType() {
        return "CARD_DELETED";
    }
}
