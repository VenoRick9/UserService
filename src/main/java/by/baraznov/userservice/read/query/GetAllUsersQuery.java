package by.baraznov.userservice.read.query;

import by.baraznov.userservice.mediator.Query;
import org.springframework.data.domain.Pageable;

public record GetAllUsersQuery(
        Pageable pageable
) implements Query {
    public static GetAllUsersQuery toQuery(Pageable pageable) {
        return new GetAllUsersQuery(pageable);
    }
}
