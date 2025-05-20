package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/author")
@RequiredArgsConstructor
@Slf4j
public class AuthorEndpoint {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> getAllAuthors() {
        List<AuthorResponseDto> authorResponseDtoList = authorService.getAll();
        log.info("authors: {}", authorResponseDtoList);
        return ResponseEntity.ok(authorResponseDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable int id) {
        AuthorResponseDto authorResponseDto = authorService.getById(id);
        log.info("author: {}", authorResponseDto);
        return ResponseEntity.ok(authorResponseDto);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody SaveAuthorRequest saveAuthorRequest) {
        log.info("saveAuthorRequest: {}", saveAuthorRequest);
        if (saveAuthorRequest == null) {
            return ResponseEntity.badRequest().body(null);
        }
         return authorService.save(saveAuthorRequest);
    }
}