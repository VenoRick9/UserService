package by.baraznov.userservice.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Mediator {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Autowired
    public Mediator(List<CommandHandler<?, ?>> handlerBeans) {
        for (CommandHandler<?, ?> handler : handlerBeans) {
            Class<?> commandType = resolveCommandType(handler);
            if (commandType != null) {
                handlers.put(commandType, handler);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <R, C extends Command> R send(C command) {
        CommandHandler<C, R> handler = (CommandHandler<C, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalStateException("No handler found for command: " + command.getClass().getSimpleName());
        }
        return handler.handle(command);
    }

    private Class<?> resolveCommandType(CommandHandler<?, ?> handler) {
        ResolvableType resolvableType = ResolvableType.forClass(handler.getClass())
                .as(CommandHandler.class)
                .getGeneric(0);

        if (resolvableType != ResolvableType.NONE) {
            return resolvableType.resolve();
        }
        return null;
    }

}
