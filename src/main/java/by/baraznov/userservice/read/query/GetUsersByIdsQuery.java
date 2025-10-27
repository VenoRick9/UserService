package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;

import java.util.List;

public record GetUsersByIdsQuery(
        List<String> ids
) implements Query {
    public static GetUsersByIdsQuery toQuery(List<String> ids) {
        return new GetUsersByIdsQuery(ids);
    }
}
