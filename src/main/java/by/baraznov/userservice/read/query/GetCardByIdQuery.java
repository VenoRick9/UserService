package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;

public record GetCardByIdQuery(
        Integer id
) implements Query {
    public static GetCardByIdQuery toQuery(Integer id) {
        return new GetCardByIdQuery(id);
    }
}
