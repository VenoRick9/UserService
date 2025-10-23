package by.baraznov.userservice.dto.user;

import by.baraznov.userservice.dto.card.CardGetDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserGetDTO(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        List<CardGetDTO> cards
) implements Serializable {
}
