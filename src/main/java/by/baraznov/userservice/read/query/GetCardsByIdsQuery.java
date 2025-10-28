package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;

import java.util.List;

public record GetCardsByIdsQuery(
        List<Integer> ids
) implements Query {
    public static GetCardsByIdsQuery toQuery(List<Integer> ids) {
        return new GetCardsByIdsQuery(ids);
    }
}
