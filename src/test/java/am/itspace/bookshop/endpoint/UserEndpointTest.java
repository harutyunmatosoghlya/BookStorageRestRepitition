package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.entity.User;
import am.itspace.bookshop.entity.UserType;
import am.itspace.bookshop.repository.UserRepository;
import am.itspace.bookshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "qwerty", roles = "ADMIN")
@ActiveProfiles("test")
public class UserEndpointTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private String userJsonRegister;
    private String userJsonLogin;
    private String userJsonUpdate;
    private int userUpdateId;
    private int userDeleteId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        SaveUserRequest saveUserRequest = SaveUserRequest.builder()
                .name("admin")
                .surname("admin")
                .email("admin@gmail.com")
                .password("admin")
                .userType(UserType.ADMIN)
                .build();
        userService.save(saveUserRequest);
        SaveUserRequest saveUserRequestForUpdate = SaveUserRequest.builder()
                .name("user")
                .surname("user")
                .email("user@gmail.com")
                .password("user")
                .userType(UserType.USER)
                .build();
        User userUpdate = userService.save(saveUserRequestForUpdate);
        userUpdateId = userUpdate.getId();
        SaveUserRequest saveUserRequestForDelete = SaveUserRequest.builder()
                .name("user1")
                .surname("user1")
                .email("user1@gmail.com")
                .password("user1")
                .userType(UserType.USER)
                .build();
        User userDelete = userService.save(saveUserRequestForDelete);
        userDeleteId = userDelete.getId();
        userJsonRegister = """
                {
                "name": "adminJSON",
                "surname": "adminJSON",
                "email": "json@gmail.com",
                "password": "json",
                "userType": "USER"
                }
                """;
        userJsonLogin = """
                {
                "email": "admin@gmail.com",
                "password": "admin"
                }
                """;
        userJsonUpdate = """
                {
                "name": "adminJSON",
                "surname": "adminJSON",
                "email": "",
                "password": "json",
                "userType": "USER"
                }
                """;
    }

    @Test
    void login() throws Exception {
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJsonLogin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    void register() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJsonRegister))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("adminJSON"));
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/user/{id}", userUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJsonUpdate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("adminJSON"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/user/{id}", userDeleteId))
                .andDo(print())
                .andExpect(status().isOk());
    }
}