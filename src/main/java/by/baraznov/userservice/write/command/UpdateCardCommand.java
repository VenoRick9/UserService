package by.baraznov.userservice.write.command;

import by.baraznov.userservice.mediator.Command;

import java.time.LocalDate;

public record UpdateCardCommand(
        Integer id,
        String number,
        String holder,
        LocalDate expirationDate
) implements Command {
    public static UpdateCardCommand toCommand(Integer id, String number, String holder,
                                              LocalDate expirationDate) {
        return new UpdateCardCommand(id, number, holder, expirationDate);
    }
}
