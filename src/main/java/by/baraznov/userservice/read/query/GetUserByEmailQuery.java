package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;

public record GetUserByEmailQuery (
        String email
) implements Query {
    public static GetUserByEmailQuery toQuery(String email) {
        return new GetUserByEmailQuery(email);
    }
}
