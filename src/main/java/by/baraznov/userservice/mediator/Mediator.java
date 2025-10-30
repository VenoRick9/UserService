package by.baraznov.userservice.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Mediator {

    private final Map<Class<?>, CommandHandler<?, ?>> commandHandlers = new ConcurrentHashMap<>();
    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers = new ConcurrentHashMap<>();

    @Autowired
    public Mediator(List<CommandHandler<?, ?>> commandHandlerBeans,
                    List<QueryHandler<?, ?>> queryHandlerBeans) {
        for (CommandHandler<?, ?> handler : commandHandlerBeans) {
            Class<?> commandType = resolveGenericType(handler, 0);
            if (commandType != null) commandHandlers.put(commandType, handler);
        }
        for (QueryHandler<?, ?> handler : queryHandlerBeans) {
            Class<?> queryType = resolveGenericType(handler, 0);
            if (queryType != null) queryHandlers.put(queryType, handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <R, C extends Command> R send(C command) {
        CommandHandler<C, R> handler = (CommandHandler<C, R>) commandHandlers.get(command.getClass());
        if (handler == null) throw new IllegalStateException("No handler found for command: " + command.getClass().getSimpleName());
        return handler.handle(command);
    }

    @SuppressWarnings("unchecked")
    public <R, Q extends Query> R send(Q query) {
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) queryHandlers.get(query.getClass());
        if (handler == null) throw new IllegalStateException("No handler found for query: " + query.getClass().getSimpleName());
        return handler.handle(query);
    }

    private Class<?> resolveGenericType(Object handler, int genericIndex) {
        ResolvableType type = ResolvableType.forClass(handler.getClass()).as(handler instanceof CommandHandler ? CommandHandler.class : QueryHandler.class);
        return type.getGeneric(genericIndex).resolve();
    }
}