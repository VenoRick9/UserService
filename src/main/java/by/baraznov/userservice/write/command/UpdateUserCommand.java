package by.baraznov.userservice.write.command;

import by.baraznov.userservice.dto.user.UserUpdateDTO;
import by.baraznov.userservice.mediator.Command;

import java.time.LocalDate;
import java.util.UUID;


public record UpdateUserCommand(
         UUID id,
         String name,
         String surname,
         LocalDate birthDate,
         String email
) implements Command {

    public static UpdateUserCommand fromDTO(UUID id, UserUpdateDTO userUpdateDTO) {
        return new UpdateUserCommand(id, userUpdateDTO.name(),
                userUpdateDTO.surname(), userUpdateDTO.birthDate(), userUpdateDTO.email());
    }
}
