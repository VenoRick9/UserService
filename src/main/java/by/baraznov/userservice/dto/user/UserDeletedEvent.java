package by.baraznov.userservice.dto.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDeletedEvent(
        UUID id
) implements UserEvent {
    @Override
    public String eventType() {
        return "USER_DELETED";
    }
}
