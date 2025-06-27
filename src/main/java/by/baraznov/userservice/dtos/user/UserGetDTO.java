package by.baraznov.userservice.dtos.user;

import by.baraznov.userservice.dtos.card.CardGetDTO;

import java.time.LocalDate;
import java.util.List;

public record UserGetDTO(
        Integer id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        List<CardGetDTO> cards
) {
}
