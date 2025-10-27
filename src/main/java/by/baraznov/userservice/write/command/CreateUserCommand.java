package by.baraznov.userservice.write.command;

import by.baraznov.userservice.mediator.Command;

import java.time.LocalDate;
import java.util.UUID;


public record CreateUserCommand(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email
) implements Command {
}
