package by.baraznov.userservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record UserCreateDTO(
        @NotNull
        UUID id,
        @Size(min = 2, max = 15, message = "The name must contain between 2 and 15 characters")
        @NotBlank
        String name,
        @Size(min = 2, max = 25, message = "The surname must contain between 2 and 25 characters")
        @NotBlank
        String surname,
        @NotNull
        LocalDate birthDate,
        @Pattern(regexp = "^[A-Za-z0-9._-]+@[a-z]+\\.[a-z]+$", message = "Invalid mail format")
        @NotBlank
        String email
) {
}
