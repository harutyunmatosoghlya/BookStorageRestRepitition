package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserAuthRequest;
import am.itspace.bookshop.dto.UserAuthResponse;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(userService.save(saveUserRequest));
    }
}