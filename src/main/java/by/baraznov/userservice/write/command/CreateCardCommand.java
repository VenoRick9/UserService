package by.baraznov.userservice.write.command;

import by.baraznov.userservice.mediator.Command;

import java.time.LocalDate;

public record CreateCardCommand(
        String number,
        String holder,
        LocalDate expirationDate,
        String token
) implements Command {
    public static CreateCardCommand toCommand(String number, String holder,
                                              LocalDate expirationDate, String token) {
        return new CreateCardCommand(number, holder, expirationDate, token);
    }
}
