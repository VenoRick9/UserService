package by.baraznov.userservice.dtos.card;

import java.time.LocalDate;

public record CardGetDTO(
        Integer id,
        String number,
        String holder,
        LocalDate expirationDate
) {
}
