package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Gender;
import am.itspace.bookshop.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@WithMockUser(username = "admin", roles = "ADMIN")
@AutoConfigureMockMvc
class AuthorEndpointTest {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;
    private final Date date = new Date();
    private String authorJson;
    private String authorJsonUpdate;

    @BeforeEach
    void setUp() {
        SaveAuthorRequest saveAuthorRequest = SaveAuthorRequest.builder()
                .name("author")
                .surname("author")
                .phone("123456789")
                .dateOfBirthday(date)
                .gender(Gender.MALE)
                .build();
        authorService.save(saveAuthorRequest);
        SaveAuthorRequest saveAuthorRequestForDelete = SaveAuthorRequest.builder()
                .name("author")
                .surname("author")
                .phone("123456789")
                .dateOfBirthday(date)
                .gender(Gender.MALE)
                .build();
        authorService.save(saveAuthorRequestForDelete);
        SaveAuthorRequest saveAuthorRequestForUpdate = SaveAuthorRequest.builder()
                .name("author")
                .surname("author")
                .phone("123456789")
                .dateOfBirthday(date)
                .gender(Gender.MALE)
                .build();
        authorService.save(saveAuthorRequestForUpdate);
        authorJson = """
                        {
                        "name": "authorJSON",
                        "surname": "authorJSON",
                        "phone": "987654321",
                        "dateOfBirthday": "1998-12-31",
                        "gender": "FEMALE"
                        }
                """;
        authorJsonUpdate = """
                        {
                        "name": "authorJSON",
                        "surname": "",
                        "phone": "987654321",
                        "dateOfBirthday": "1998-12-31",
                        "gender": "FEMALE"
                        }
                """;
    }

    @Test
    void getAllAuthors() throws Exception {
        mockMvc.perform(get("/author"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("author"))
                .andExpect(jsonPath("$[0].surname").value("author"))
                .andExpect(jsonPath("$[0].phone").value("123456789"))
                .andExpect(jsonPath("$[0].gender").value("MALE"));
    }

    @Test
    void getAuthorById() throws Exception {
        mockMvc.perform(get("/author/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("author"))
                .andExpect(jsonPath("$.surname").value("author"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.gender").value("MALE"));
    }

    @Test
    void add() throws Exception {
        mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("authorJSON"))
                .andExpect(jsonPath("$.surname").value("authorJSON"));
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/author/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJsonUpdate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("authorJSON"))
                .andExpect(jsonPath("$.surname").value("author"));
    }

    @Test
    void deleteAuthor() throws Exception {
        mockMvc.perform(delete("/author/{id}", 2))
                .andDo(print())
                .andExpect(status().isOk());
    }
}