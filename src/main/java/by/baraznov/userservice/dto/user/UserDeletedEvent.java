package by.baraznov.userservice.dto.user;

import by.baraznov.userservice.dto.Event;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDeletedEvent(
        UUID id
) implements Event {
    @Override
    public String eventType() {
        return "USER_DELETED";
    }
}
