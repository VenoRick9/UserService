package by.baraznov.userservice.write.command;

import by.baraznov.userservice.mediator.Command;

import java.util.UUID;


public record DeleteUserCommand(
        UUID id
) implements Command {
    public static DeleteUserCommand toCommand(UUID id) {
        return new DeleteUserCommand(id);
    }
}
