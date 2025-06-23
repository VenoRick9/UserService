package by.baraznov.userservice.dtos.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardCreateDTO(
        @Size(min = 16, max = 16)
        @NotNull
        String number,
        @Pattern(regexp = "^[A-Z]{1,25}\\s[A-Z]{1,15}$")
        @NotBlank
        String holder,
        @NotNull
        LocalDate expirationDate
) {
}
