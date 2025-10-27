package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserQueryUserGetDTOMapper;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.query.GetUserByIdQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.util.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, UserGetDTO> {
    private final UserQueryRepository userQueryRepository;
    private final UserQueryUserGetDTOMapper userQueryUserGetDTOMapper;

    @Override
    @Cacheable(value = "user", key = "#query.id()")
    public UserGetDTO handle(GetUserByIdQuery query) {
        if (query.id() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return userQueryUserGetDTOMapper.toDto(userQueryRepository.findById(query.id())
                .orElseThrow(() -> new UserNotFound("User with id " + query.id() + " doesn't exist")));
    }
}
