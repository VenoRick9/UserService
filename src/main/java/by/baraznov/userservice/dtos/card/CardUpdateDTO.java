package by.baraznov.userservice.dtos.card;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardUpdateDTO(
        @Size(min = 16, max = 16, message = "The number must contain 16 characters")
        String number,
        @Pattern(regexp = "^[A-Z]{1,25}\\s[A-Z]{1,15}$", message = "The holder must contains only upper case letters")
        String holder,
        LocalDate expirationDate
) {
}
