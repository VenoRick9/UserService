package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserQueryUserGetDTOMapper;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.query.GetUserByEmailQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.util.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetUserByEmailQueryHandler implements QueryHandler<GetUserByEmailQuery, UserGetDTO> {
    private final UserQueryRepository userQueryRepository;
    private final UserQueryUserGetDTOMapper userQueryUserGetDTOMapper;

    @Override
    //@Cacheable(value = "user", key = "#query.email()")
    public UserGetDTO handle(GetUserByEmailQuery query) {
        if (query.email() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userQueryUserGetDTOMapper.toDto(userQueryRepository.findByEmail(query.email())
                .orElseThrow(() -> new UserNotFound("User with email " + query.email() + " doesn't exist")));
    }
}
