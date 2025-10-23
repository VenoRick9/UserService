package by.baraznov.userservice.dto.card;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record CardGetDTO(
        Integer id,
        UUID userId,
        String number,
        String holder,
        LocalDate expirationDate
) implements Serializable {
}
