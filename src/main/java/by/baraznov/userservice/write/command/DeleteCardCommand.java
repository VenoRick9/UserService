package by.baraznov.userservice.write.command;

import by.baraznov.userservice.mediator.Command;


public record DeleteCardCommand(
        Integer id
) implements Command {
    public static DeleteCardCommand toCommand(Integer id) {
        return new DeleteCardCommand(id);
    }
}
