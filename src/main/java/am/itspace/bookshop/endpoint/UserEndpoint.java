package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserEndpoint {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@RequestBody UserAuthRequest userAuthRequest) {
        log.info("Login request: {}", userAuthRequest);
        if (userAuthRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.login(userAuthRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody SaveUserRequest saveUserRequest) {
        log.info("Register request: {}", saveUserRequest);
        if (saveUserRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(userService.save(saveUserRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserUpdateResponse> update(@PathVariable int id, @RequestBody SaveUserRequest saveUserRequest) {
        log.info("Update request: {}", saveUserRequest);
        if (saveUserRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.update(saveUserRequest, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        log.info("Delete request: {}", id);
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}