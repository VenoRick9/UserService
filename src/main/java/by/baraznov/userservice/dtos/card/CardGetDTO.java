package by.baraznov.userservice.dtos.card;

import java.io.Serializable;
import java.time.LocalDate;

public record CardGetDTO(
        Integer id,
        Integer userId,
        String number,
        String holder,
        LocalDate expirationDate
) implements Serializable {
}
