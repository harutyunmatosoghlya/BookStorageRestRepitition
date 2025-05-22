package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.mapper.UserMapper;
import am.itspace.bookshop.repository.UserRepository;
import am.itspace.bookshop.service.UserService;
import am.itspace.bookshop.util.JwtTokenUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserAuthResponse> login(UserAuthRequest userAuthRequest) {
        Optional<User> userOptional = userRepository.findByEmail(userAuthRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(userAuthRequest.getPassword(), user.getPassword())) {
                UserAuthResponse response = UserAuthResponse.builder()
                        .token(jwtTokenUtil.generateToken(user.getEmail()))
                        .name(user.getName())
                        .surname(user.getSurname())
                        .userId(user.getId())
                        .build();
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User save(SaveUserRequest saveUserRequest) {
        User user = userMapper.toEntity(saveUserRequest);
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<UserUpdateResponse> update(SaveUserRequest saveUserRequest, int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = updateUserFields(user.get(), saveUserRequest);
            return new ResponseEntity<>(userMapper.toDto(userRepository.save(updatedUser)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void delete(int id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    private User updateUserFields(User user, SaveUserRequest saveUserRequest) {
        user.setName(getOrDefault(user.getName(), saveUserRequest.getName()));
        user.setSurname(getOrDefault(user.getSurname(), saveUserRequest.getSurname()));
        user.setEmail(getOrDefault(user.getEmail(), saveUserRequest.getEmail()));
        user.setPassword(getOrDefault(user.getPassword(), passwordEncoder.encode(saveUserRequest.getPassword())));
        user.setUserType(getOrDefault(user.getUserType(), saveUserRequest.getUserType()));
        return user;
    }

    private <T> T getOrDefault(T current, T incoming) {
        if (incoming == null) return current;
        if (incoming instanceof String && ((String) incoming).trim().isEmpty()) return current;
        return incoming;
    }
}