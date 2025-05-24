package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.entity.UserType;
import am.itspace.bookshop.mapper.UserMapper;
import am.itspace.bookshop.repository.UserRepository;
import am.itspace.bookshop.util.JwtTokenUtil;
import am.itspace.bookshop.util.ValueUpdateUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ValueUpdateUtil valueUpdateUtil;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        User user = User.builder().id(1).name("Example").surname("Exampleyan").email("example@mail.com").password("Example").build();
        when(userRepository.findByEmail("example@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "Example")).thenReturn(true);
        when(jwtTokenUtil.generateToken("example@mail.com")).thenReturn("jwt-token");
        UserAuthRequest request = new UserAuthRequest("example@mail.com", "123");
        ResponseEntity<UserAuthResponse> response = userService.login(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
    }

    @Test
    void testLoginWrongPassword() {
        User user = User.builder().email("example@mail.com").password("Example").build();
        when(userRepository.findByEmail("example@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "Example")).thenReturn(false);
        ResponseEntity<UserAuthResponse> response = userService.login(new UserAuthRequest("example@mail.com", "wrong"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());
        ResponseEntity<UserAuthResponse> response = userService.login(new UserAuthRequest("notfound@mail.com", "123"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testSaveNewUser() {
        SaveUserRequest request = new SaveUserRequest("Example1", "Exampleyan1", "example1@mail.com", "Example1", UserType.USER);
        User user = User.builder()
                .name("Example1")
                .surname("Exampleyan1")
                .email("example1@mail.com")
                .userType(UserType.USER)
                .build();
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.findByEmail("example1@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Example1")).thenReturn("Example");
        User savedUser = User.builder()
                .name("Example1")
                .surname("Exampleyan1")
                .email("example1@mail.com")
                .userType(UserType.USER)
                .password("Example")
                .build();
        when(userRepository.save(user)).thenReturn(savedUser);
        User result = userService.save(request);
        assertThat(result.getPassword()).isEqualTo("Example");
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateFound() {
        SaveUserRequest request = new SaveUserRequest("NewName", "NewSurname", "new@mail.com", "newpass", UserType.USER);
        User existingUser = User.builder().id(1).name("Old").surname("Old").email("old@mail.com").userType(UserType.USER).build();
        User updatedUser = User.builder().id(1).name("NewName").surname("NewSurname").email("new@mail.com").userType(UserType.USER).password("encoded").build();
        UserUpdateResponse dto = new UserUpdateResponse("NewName", "NewSurname", "new@mail.com");
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(valueUpdateUtil.getOrDefault("Old", "NewName")).thenReturn("NewName");
        when(valueUpdateUtil.getOrDefault("Old", "NewSurname")).thenReturn("NewSurname");
        when(valueUpdateUtil.getOrDefault("old@mail.com", "new@mail.com")).thenReturn("new@mail.com");
        when(valueUpdateUtil.getOrDefault(UserType.USER, UserType.USER)).thenReturn(UserType.USER);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.toDto(any())).thenReturn(dto);
        ResponseEntity<UserUpdateResponse> response = userService.update(request, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void testUpdateNotFound() {
        SaveUserRequest request = new SaveUserRequest();
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        ResponseEntity<UserUpdateResponse> result = userService.update(request, 99);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteSuccess() {
        when(userRepository.existsById(1)).thenReturn(true);
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound() {
        when(userRepository.existsById(1)).thenReturn(false);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.delete(1));
        assertThat(ex.getMessage()).contains("User with id 1 not found");
    }
}