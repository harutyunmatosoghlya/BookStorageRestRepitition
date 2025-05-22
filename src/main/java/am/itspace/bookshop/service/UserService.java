package am.itspace.bookshop.service;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    ResponseEntity<UserAuthResponse> login(UserAuthRequest userAuthRequest);

    Optional<User> findByEmail(String username);

    User save(SaveUserRequest saveUserRequest);

    ResponseEntity<UserUpdateResponse> update(SaveUserRequest saveUserRequest, int id);

    void delete(int id);
}