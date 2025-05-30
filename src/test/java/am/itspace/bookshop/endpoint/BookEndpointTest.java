package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.entity.Gender;
import am.itspace.bookshop.service.AuthorService;
import am.itspace.bookshop.service.BookService;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = "ADMIN")
public class BookEndpointTest {
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;
    private final Date date = new Date();
    private String bookJson;
    private String bookJsonUpdate;

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
        SaveBookRequest saveBookRequest = SaveBookRequest.builder()
                .title("book1")
                .price(101)
                .qty(101)
                .authorId(1)
                .build();
        bookService.save(saveBookRequest);
        SaveBookRequest saveBookRequestForUpdate = SaveBookRequest.builder()
                .title("book2")
                .price(102)
                .qty(102)
                .authorId(1)
                .build();
        bookService.save(saveBookRequestForUpdate);
        SaveBookRequest saveBookRequestForDelete = SaveBookRequest.builder()
                .title("book3")
                .price(103)
                .qty(103)
                .authorId(1)
                .build();
        bookService.save(saveBookRequestForDelete);
        bookJson = """
                        {
                        "title": "bookJSON",
                        "price": 50,
                        "qty": 50,
                        "authorId": 1
                        }
                """;
        bookJsonUpdate = """
                        {
                        "title": "",
                        "price": 50,
                        "qty": 50,
                        "authorId": 1
                        }
                """;
    }


    @Test
    void getAllBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("book1"))
                .andExpect(jsonPath("$[0].price").value(101))
                .andExpect(jsonPath("$[0].author.name").value("author"));
    }

    @Test
    void add() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("bookJSON"))
                .andExpect(jsonPath("$.price").value(50))
                .andExpect(jsonPath("$.author.name").value("author"));
    }

    @Test
    void addNull() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookById() throws Exception {
        mockMvc.perform(get("/books/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("book1"))
                .andExpect(jsonPath("$.price").value(101))
                .andExpect(jsonPath("$.author.name").value("author"));
    }

    @Test
    void getBookByAuthorId() throws Exception {
        mockMvc.perform(get("/books/author/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("book1"))
                .andExpect(jsonPath("$[0].price").value(101))
                .andExpect(jsonPath("$[0].author.name").value("author"));
    }

    @Test
    void updateBook() throws Exception {
        mockMvc.perform(put("/books/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJsonUpdate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("book2"))
                .andExpect(jsonPath("$.price").value(50))
                .andExpect(jsonPath("$.author.name").value("author"));
    }

    @Test
    void updateBookNull() throws Exception {
        mockMvc.perform(put("/books/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", 3))
                .andDo(print())
                .andExpect(status().isOk());
    }
}