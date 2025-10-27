package by.baraznov.userservice.write.command;

import by.baraznov.userservice.dto.user.UserUpdateDTO;
import by.baraznov.userservice.mediator.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserCommand implements Command {
    private UUID id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    public static UpdateUserCommand fromDTO(UUID id, UserUpdateDTO userUpdateDTO) {
        return new UpdateUserCommand(id, userUpdateDTO.name(),
                userUpdateDTO.surname(), userUpdateDTO.birthDate(), userUpdateDTO.email());
    }
}
