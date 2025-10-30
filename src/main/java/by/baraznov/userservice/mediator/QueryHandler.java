package by.baraznov.userservice.mediator;

public interface QueryHandler<Q extends Query, R> {
    R handle(Q query);
}
