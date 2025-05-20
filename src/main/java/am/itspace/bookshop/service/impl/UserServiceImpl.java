package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.mapper.UserMapper;
import am.itspace.bookshop.repository.UserRepository;
import am.itspace.bookshop.service.UserService;
import am.itspace.bookshop.util.JwtTokenUtil;
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
}