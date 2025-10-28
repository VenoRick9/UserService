package by.baraznov.userservice.dto.user;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UserUpdatedEvent(
        UUID id,
        String name,
        String surname,
        String email,
        LocalDate birthDate
) implements Event {
    @Override
    public String eventType() {
        return "USER_UPDATED";
    }
}
