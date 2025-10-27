package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;

public record GetUserByIdQuery(
        String id
) implements Query {
    public static GetUserByIdQuery toQuery(String id) {
        return new GetUserByIdQuery(id);
    }
}
