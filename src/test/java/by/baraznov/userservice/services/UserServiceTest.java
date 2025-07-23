package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.dtos.user.UserUpdateDTO;
import by.baraznov.userservice.mappers.user.UserCreateDTOMapper;
import by.baraznov.userservice.mappers.user.UserGetDTOMapper;
import by.baraznov.userservice.mappers.user.UserUpdateDTOMapper;
import by.baraznov.userservice.models.User;
import by.baraznov.userservice.repositories.UserRepository;
import by.baraznov.userservice.services.impl.UserServiceImpl;
import by.baraznov.userservice.utils.EmailAlreadyExist;
import by.baraznov.userservice.utils.UserAlreadyExist;
import by.baraznov.userservice.utils.UserNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGetDTOMapper userGetDTOMapper;
    @Mock
    private UserCreateDTOMapper userCreateDTOMapper;
    @Mock
    private UserUpdateDTOMapper userUpdateDTOMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void test_getAllUsers() {
        Pageable pageable = PageRequest.of(0, 2);
        User user1 = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        User user2 = new User(2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        UserGetDTO dto1 = new UserGetDTO(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO dto2 = new UserGetDTO(2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        List<User> users = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userGetDTOMapper.toDto(user1)).thenReturn(dto1);
        when(userGetDTOMapper.toDto(user2)).thenReturn(dto2);

        Page<UserGetDTO> result = userService.getAllUsers(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(userRepository).findAll(pageable);
        verify(userGetDTOMapper).toDto(user1);
        verify(userGetDTOMapper).toDto(user2);
    }

    @Test
    public void test_getUserById() {
        Integer userId = 1;
        User user = new User(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO dto = new UserGetDTO(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userGetDTOMapper.toDto(user)).thenReturn(dto);

        UserGetDTO result = userService.getUserById(userId);

        assertEquals(dto, result);
        verify(userRepository).findById(userId);
        verify(userGetDTOMapper).toDto(user);
    }

    @Test
    public void test_getUsersByIds() {
        User user1 = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        User user2 = new User(2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        UserGetDTO dto1 = new UserGetDTO(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO dto2 = new UserGetDTO(2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        List<User> users = List.of(user1, user2);
        when(userRepository.findUsersByIds(List.of(user1.getId(), user2.getId()))).thenReturn(users);
        when(userGetDTOMapper.toDtos(users)).thenReturn(List.of(dto1, dto2));

        List<UserGetDTO> result = userService.getUsersByIds(List.of(user1.getId(), user2.getId()));

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(userRepository).findUsersByIds(List.of(user1.getId(), user2.getId()));
        verify(userGetDTOMapper).toDtos(List.of(user1, user2));
    }

    @Test
    public void test_getUserByEmail() {
        Integer userId = 1;
        String userEmail = "john@example.com";
        User user = new User(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), userEmail, List.of());
        UserGetDTO dto = new UserGetDTO(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), userEmail, List.of());
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userGetDTOMapper.toDto(user)).thenReturn(dto);

        UserGetDTO result = userService.getUserByEmail(userEmail);

        assertEquals(dto, result);
        verify(userRepository).findUserByEmail(userEmail);
        verify(userGetDTOMapper).toDto(user);
    }

    @Test
    public void test_createUser() {
        Integer userId = 1;
        UserCreateDTO createDTO = new UserCreateDTO("John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com");
        User user = new User(null, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        User savedUser = new User(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO getDTO = new UserGetDTO(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1);
        when(userCreateDTOMapper.toEntity(createDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userGetDTOMapper.toDto(userCaptor.capture())).thenReturn(getDTO);

        UserGetDTO result = userService.create(createDTO, authentication);

        assertEquals(getDTO, result);
        User capturedUser = userCaptor.getValue();
        assertEquals("John", capturedUser.getName());
        assertEquals("Doe", capturedUser.getSurname());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), capturedUser.getBirthDate());
        verify(userRepository).save(user);
        verify(userCreateDTOMapper).toEntity(createDTO);
        verify(userGetDTOMapper).toDto(capturedUser);
    }

    @Test
    public void test_updateUser() {
        Integer userId = 1;
        UserUpdateDTO updateDTO = new UserUpdateDTO("Bridge", null, null, null);
        User user = new User(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO getDTO = new UserGetDTO(userId, "Bridge", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setName("Bridge");
            return null;
        }).when(userUpdateDTOMapper).merge(eq(user), eq(updateDTO));
        when(userRepository.save(user)).thenReturn(user);
        when(userGetDTOMapper.toDto(user)).thenReturn(getDTO);

        UserGetDTO result = userService.update(updateDTO, userId);

        assertEquals(getDTO, result);
        assertEquals("Bridge", user.getName());
        verify(userRepository).findById(userId);
        verify(userUpdateDTOMapper).merge(user, updateDTO);
        verify(userRepository).save(user);
        verify(userGetDTOMapper).toDto(user);
    }

    @Test
    public void test_deleteUser() {
        Integer userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }


    @Test
    public void test_getUserById_shouldThrowException() {
        Integer userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }
    @Test
    public void test_createUser_shouldThrowEmailAlreadyExist() {
        Integer userId = 1;
        String email = "existing@example.com";
        UserCreateDTO createDTO = new UserCreateDTO("John", "Doe", LocalDate.of(1990, 1, 1), email);
        User user = new User(null, "John", "Doe", LocalDate.of(1990, 1, 1), email, List.of());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        when(userCreateDTOMapper.toEntity(createDTO)).thenReturn(user);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user)); // <- email exists

        assertThrows(EmailAlreadyExist.class, () -> userService.create(createDTO, authentication));

        verify(userRepository).findUserByEmail(email);
        verify(userCreateDTOMapper).toEntity(createDTO);
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    public void test_createUser_shouldThrowUserAlreadyExist() {
        Integer userId = 1;
        String email = "new@example.com";
        UserCreateDTO createDTO = new UserCreateDTO("John", "Doe", LocalDate.of(1990, 1, 1), email);
        User user = new User(null, "John", "Doe", LocalDate.of(1990, 1, 1), email, List.of());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        when(userCreateDTOMapper.toEntity(createDTO)).thenReturn(user);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExist.class, () -> userService.create(createDTO, authentication));

        verify(userRepository).findUserByEmail(email);
        verify(userRepository).findById(userId);
        verify(userCreateDTOMapper).toEntity(createDTO);
        verifyNoMoreInteractions(userRepository);
    }

}