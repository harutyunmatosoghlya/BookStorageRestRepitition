package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.entity.UserType;
import am.itspace.bookshop.exaption.EmailAlreadyExistException;
import am.itspace.bookshop.exaption.IncorrectPasswordException;
import am.itspace.bookshop.exaption.UserNotFoundException;
import am.itspace.bookshop.mapper.UserMapper;
import am.itspace.bookshop.repository.UserRepository;
import am.itspace.bookshop.util.JwtTokenUtil;
import am.itspace.bookshop.util.ValueUpdateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void loginSuccess() {
        UserAuthRequest request = new UserAuthRequest("test@mail.com", "password");
        User user = User.builder().id(1).email("test@mail.com").password("encoded").name("John").surname("Doe").build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtTokenUtil.generateToken("test@mail.com")).thenReturn("jwt-token");
        ResponseEntity<UserAuthResponse> response = userService.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserAuthResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.getToken());
        assertEquals("John", body.getName());
        assertEquals("Doe", body.getSurname());
        assertEquals(1, body.getUserId());
    }

    @Test
    void loginWrongPassword() {
        UserAuthRequest request = new UserAuthRequest("test@mail.com", "wrong");
        User user = User.builder().email("test@mail.com").password("encoded").build();
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        ResponseEntity<UserAuthResponse> response = userService.login(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void loginUserNotFound() {
        UserAuthRequest request = new UserAuthRequest("unknown@mail.com", "pass");
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());
        ResponseEntity<UserAuthResponse> response = userService.login(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void saveSuccess() {
        SaveUserRequest request = new SaveUserRequest("John", "Doe", "john@mail.com", "pass", UserType.USER);
        User userToSave = User.builder().name("John").surname("Doe").email("john@mail.com").password("pass").userType(UserType.USER).build();
        User savedUser = User.builder().id(1).name("John").surname("Doe").email("john@mail.com").password("encoded").userType(UserType.USER).build();
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userMapper.toEntity(request)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        User result = userService.save(request);
        assertSame(savedUser, result);
        verify(userRepository).save(userToSave);
    }

    @Test
    void savePasswordIsNull() {
        SaveUserRequest request = new SaveUserRequest("John", "Doe", "john@mail.com", null, UserType.USER);
        when(userMapper.toEntity(request)).thenReturn(User.builder().password(null).build());
        assertThrows(IncorrectPasswordException.class, () -> userService.save(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveDuplicateEmail() {
        SaveUserRequest request = new SaveUserRequest("John", "Doe", "duplicate@mail.com", "pass", UserType.USER);
        when(userMapper.toEntity(request)).thenReturn(User.builder().email("duplicate@mail.com").password("pass").build());
        when(userRepository.findByEmail("duplicate@mail.com")).thenReturn(Optional.of(new User()));
        assertThrows(EmailAlreadyExistException.class, () -> userService.save(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateSuccess() {
        int userId = 1;
        SaveUserRequest request = new SaveUserRequest("NewJohn", "NewDoe", "new@mail.com", "newPass", UserType.ADMIN);
        User existingUser = User.builder().id(userId).name("OldJohn").surname("OldDoe").email("old@mail.com").password("oldEncoded").userType(UserType.USER).build();
        User updatedUser = User.builder().id(userId).name("NewJohn").surname("NewDoe").email("new@mail.com").password("newEncoded").userType(UserType.ADMIN).build();
        UserUpdateResponse responseDto = new UserUpdateResponse("NewJohn", "NewDoe", "new@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(valueUpdateUtil.getOrDefault("OldJohn", "NewJohn")).thenReturn("NewJohn");
        when(valueUpdateUtil.getOrDefault("OldDoe", "NewDoe")).thenReturn("NewDoe");
        when(valueUpdateUtil.getOrDefault("old@mail.com", "new@mail.com")).thenReturn("new@mail.com");
        when(valueUpdateUtil.getOrDefault(UserType.USER, UserType.ADMIN)).thenReturn(UserType.ADMIN);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        ResponseEntity<UserUpdateResponse> response = userService.update(request, userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseDto, response.getBody());
        assertEquals("new@mail.com", existingUser.getEmail());
        assertEquals("newEncoded", existingUser.getPassword());
    }

    @Test
    void updateUserNotFound() {
        SaveUserRequest request = new SaveUserRequest("John", "Doe", "john@mail.com", "pass", UserType.USER);
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<UserUpdateResponse> response = userService.update(request, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteSuccess() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserNotFound() {
        int userId = 999;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void findByEmail() {
        String email = "test@mail.com";
        User expectedUser = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
        Optional<User> result = userService.findByEmail(email);
        assertTrue(result.isPresent());
        assertSame(expectedUser, result.get());
    }

    @Test
    void updateShouldNotUpdatePasswordWhenWhitespaceOnly() {
        int userId = 1;
        SaveUserRequest request = new SaveUserRequest("John", "Doe", "john@mail.com", "   ", UserType.USER);
        User existingUser = User.builder()
                .id(userId)
                .password("oldEncoded")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(valueUpdateUtil.getOrDefault(any(), any())).thenCallRealMethod();
        userService.update(request, userId);
        assertEquals("oldEncoded", existingUser.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateShouldNotUpdatePasswordWhenWhiteNull() {
        int userId = 1;
        SaveUserRequest request = SaveUserRequest.builder()
                .name("John")
                .surname("Doe")
                .email("john@mail.com")
                .userType(UserType.USER)
                .build();
        User existingUser = User.builder()
                .id(userId)
                .password("oldEncoded")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(valueUpdateUtil.getOrDefault(any(), any())).thenCallRealMethod();
        userService.update(request, userId);
        assertEquals("oldEncoded", existingUser.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }
}