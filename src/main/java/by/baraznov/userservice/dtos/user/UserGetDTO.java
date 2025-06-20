package by.baraznov.userservice.dtos.user;

import java.time.LocalDate;

public record UserGetDTO(
        Integer id,
        String name,
        String surname,
        LocalDate birthDate,
        String email
) {
}
