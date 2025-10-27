package by.baraznov.userservice.service;

import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.dto.user.UserUpdateDTO;
import by.baraznov.userservice.mapper.user.UserCreateDTOMapper;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mapper.user.UserUpdateDTOMapper;
import by.baraznov.userservice.service.impl.UserServiceImpl;
import by.baraznov.userservice.util.EmailAlreadyExist;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.model.UserCommand;
import by.baraznov.userservice.write.repository.UserCommandRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserCommandRepository userRepository;

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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        UserCommand user1 = new UserCommand(id1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserCommand user2 = new UserCommand(id2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        UserGetDTO dto1 = new UserGetDTO(id1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO dto2 = new UserGetDTO(id2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        List<UserCommand> users = List.of(user1, user2);
        Page<UserCommand> userPage = new PageImpl<>(users, pageable, users.size());

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
        UUID userId = UUID.randomUUID();
        UserCommand user = new UserCommand(userId, "John", "Doe",
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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UserCommand user1 = new UserCommand(id1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserCommand user2 = new UserCommand(id2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        UserGetDTO dto1 = new UserGetDTO(id1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO dto2 = new UserGetDTO(id2, "Jane", "Smith",
                LocalDate.of(1985, 5, 5), "jane@example.com", List.of());
        List<UserCommand> users = List.of(user1, user2);

        //when(userRepository.findUsersByIds(List.of(id1, id2))).thenReturn(users);
        when(userGetDTOMapper.toDtos(users)).thenReturn(List.of(dto1, dto2));

        List<UserGetDTO> result = userService.getUsersByIds(List.of(id1, id2));

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        //verify(userRepository).findUsersByIds(List.of(id1, id2));
        verify(userGetDTOMapper).toDtos(List.of(user1, user2));
    }

    @Test
    public void test_getUserByEmail() {
        UUID userId = UUID.randomUUID();
        String userEmail = "john@example.com";
        UserCommand user = new UserCommand(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), userEmail, List.of());
        UserGetDTO dto = new UserGetDTO(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), userEmail, List.of());

        //when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userGetDTOMapper.toDto(user)).thenReturn(dto);

        UserGetDTO result = userService.getUserByEmail(userEmail);

        assertEquals(dto, result);
       // verify(userRepository).findUserByEmail(userEmail);
        verify(userGetDTOMapper).toDto(user);
    }

    @Test
    public void test_createUser() {
        UUID userId = UUID.randomUUID();
        UserCreateDTO createDTO = new UserCreateDTO(userId,"John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com");
        UserCommand user = new UserCommand(null, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserCommand savedUser = new UserCommand(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO getDTO = new UserGetDTO(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());

        when(userCreateDTOMapper.toEntity(createDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        ArgumentCaptor<UserCommand> userCaptor = ArgumentCaptor.forClass(UserCommand.class);
        when(userGetDTOMapper.toDto(userCaptor.capture())).thenReturn(getDTO);

        UserGetDTO result = userService.create(createDTO);

        assertEquals(getDTO, result);
        UserCommand capturedUser = userCaptor.getValue();
        assertEquals("John", capturedUser.getName());
        assertEquals("Doe", capturedUser.getSurname());
        assertEquals("john@example.com", capturedUser.getEmail());
        verify(userRepository).save(user);
        verify(userCreateDTOMapper).toEntity(createDTO);
        verify(userGetDTOMapper).toDto(capturedUser);
    }

    @Test
    public void test_updateUser() {
        UUID userId = UUID.randomUUID();
        UserUpdateDTO updateDTO = new UserUpdateDTO("Bridge", null, null, null);
        UserCommand user = new UserCommand(userId, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        UserGetDTO getDTO = new UserGetDTO(userId, "Bridge", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            UserCommand u = invocation.getArgument(0);
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
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void test_getUserById_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    public void test_createUser_shouldThrowEmailAlreadyExist() {
        UUID userId = UUID.randomUUID();
        String email = "existing@example.com";
        UserCreateDTO createDTO = new UserCreateDTO(userId,"John", "Doe", LocalDate.of(1990, 1, 1), email);
        UserCommand user = new UserCommand(null, "John", "Doe", LocalDate.of(1990, 1, 1), email, List.of());

        when(userCreateDTOMapper.toEntity(createDTO)).thenReturn(user);
       // when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExist.class, () -> userService.create(createDTO));

       // verify(userRepository).findUserByEmail(email);
        verify(userCreateDTOMapper).toEntity(createDTO);
        verifyNoMoreInteractions(userRepository);
    }
}
