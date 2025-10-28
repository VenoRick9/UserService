package by.baraznov.userservice.dto.card;

import by.baraznov.userservice.dto.user.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardCreatedEvent(
        UUID id,
        Integer cardId,
        String number,
        String holder,
        LocalDate expirationDate
) implements Event {
    @Override
    public String eventType() {
        return "CARD_CREATED";
    }
}
