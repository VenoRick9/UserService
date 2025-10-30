package by.baraznov.userservice.dto.card;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardGetDTO(
        Integer id,
        UUID userId,
        String number,
        String holder,
        LocalDate expirationDate
) implements Serializable {
}
