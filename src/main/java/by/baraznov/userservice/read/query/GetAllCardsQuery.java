package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;
import org.springframework.data.domain.Pageable;

public record GetAllCardsQuery(
        Pageable pageable
) implements Query {
    public static GetAllCardsQuery toQuery(Pageable pageable) {
        return new GetAllCardsQuery(pageable);
    }
}
