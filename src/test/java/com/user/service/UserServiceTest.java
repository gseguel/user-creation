package com.user.service;

import com.user.dto.PhoneDto;
import com.user.dto.UserRequestDto;
import com.user.dto.UserResponseDto;
import com.user.entity.UserEntity;
import com.user.exception.EmailAlreadyExistsException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.user.util.message.UserMessage.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private JwtProvider jwtProvider;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String emailRegex = ".+@.+\\..+";
        String passwordRegex = ".{6,}";
        service = new UserServiceImpl(repo, jwtProvider, emailRegex, passwordRegex);
    }

    @Test
    void register_success() {
        UserRequestDto req = UserRequestDto.builder()
                .name("Gustavo")
                .email("gustavo.seguel@nisum.com")
                .password("123456")
                .phones(List.of(new PhoneDto("123", "1", "63")))
                .build();

        when(repo.existsByEmail(req.getEmail().toLowerCase())).thenReturn(false);
        when(jwtProvider.generateToken(req.getEmail().toLowerCase())).thenReturn("token");

        UserEntity savedEntity = UserMapper.toDtoToEntity(req);
        savedEntity.setToken("token");
        when(repo.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserResponseDto response = service.register(req);

        assertNotNull(response);
        assertEquals("Gustavo", response.getName());
        assertEquals("gustavo.seguel@nisum.com", response.getEmail());
        assertEquals("token", response.getToken());
        verify(repo, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_emailExists_throwsException() {
        UserRequestDto req = UserRequestDto.builder()
                .name("Gustavo")
                .email("gustavo.seguel@nisum.com")
                .password("123456")
                .phones(Collections.emptyList())
                .build();

        when(repo.existsByEmail(req.getEmail().toLowerCase())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.register(req));
        verify(repo, never()).save(any());
    }

    @Test
    void listAll_returnsUsers() {
        UserEntity user = new UserEntity();
        user.setName("Isabella");
        user.setEmail("isabella@nisum.com");
        when(repo.findAll()).thenReturn(List.of(user));

        List<UserResponseDto> list = service.listAll();

        assertEquals(1, list.size());
        assertEquals("Isabella", list.get(0).getName());
    }

    @Test
    void getById_found() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("Agu");
        when(repo.findById("1")).thenReturn(Optional.of(user));

        UserResponseDto response = service.getById("1");

        assertEquals("Agu", response.getName());
    }

    @Test
    void getById_notFound_throwsException() {
        when(repo.findById("1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.getById("1"));
    }

    @Test
    void delete_found() {
        UserEntity user = new UserEntity();
        user.setId("1");
        when(repo.findById("1")).thenReturn(Optional.of(user));

        service.delete("1");

        verify(repo, times(1)).delete(user);
    }

    @Test
    void delete_notFound_throwsException() {
        when(repo.findById("1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.delete("1"));
    }

    @Test
    void update_successful() {
        String userId = "123";
        UserRequestDto request = new UserRequestDto();
        request.setName("Gustavo Seguel");
        request.setEmail("gseguel@nisum.com");
        request.setPassword("newpass123");
        request.setPhones(Collections.emptyList());
        request.setActive(true);

        UserEntity existing = new UserEntity();
        existing.setId(userId);
        existing.setEmail("old@nisum.com");
        existing.setPassword("oldpass");
        existing.setPhones(new ArrayList<>());
        existing.setCreated(LocalDateTime.now());
        existing.setModified(LocalDateTime.now());
        existing.setLastLogin(LocalDateTime.now());

        when(repo.findById(userId)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("gseguel@nisum.com")).thenReturn(false);
        when(repo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = service.update(userId, request);

        assertEquals("Gustavo Seguel", result.getName());
        assertEquals("gseguel@nisum.com", result.getEmail());
        assertTrue(result.isActive());
        verify(repo).findById(userId);
        verify(repo).save(any(UserEntity.class));
    }

    @Test
    void update_userNotFound() {
        String userId = "123";
        UserRequestDto request = new UserRequestDto();

        when(repo.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.update(userId, request));
        assertEquals(USER_NOT_FOUND, ex.getMessage());
    }

    @Test
    void update_invalidEmail() {
        String userId = "123";
        UserRequestDto request = new UserRequestDto();
        request.setEmail("invalid-email");

        UserEntity existing = new UserEntity();
        existing.setId(userId);
        existing.setEmail("old@nisum.com");

        when(repo.findById(userId)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(userId, request));
        assertEquals(INVALID_EMAIL, ex.getMessage());
    }

    @Test
    void update_emailAlreadyExists() {
        String userId = "123";
        UserRequestDto request = new UserRequestDto();
        request.setEmail("duplicate@nisum.com");

        UserEntity existing = new UserEntity();
        existing.setId(userId);
        existing.setEmail("old@nisum.com");

        when(repo.findById(userId)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("duplicate@nisum.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.update(userId, request));
    }

    @Test
    void update_invalidPassword() {
        String userId = "123";
        UserRequestDto request = new UserRequestDto();
        request.setEmail("valid@nisum.com");
        request.setPassword("123"); // demasiado corta para el regex

        UserEntity existing = new UserEntity();
        existing.setId(userId);
        existing.setEmail("old@nisum.com");

        when(repo.findById(userId)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(userId, request));
        assertEquals(INVALID_PASSWORD, ex.getMessage());
    }
}