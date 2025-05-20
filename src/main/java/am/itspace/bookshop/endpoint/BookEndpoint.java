package am.itspace.bookshop.endpoint;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Slf4j
public class BookEndpoint {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        List<BookResponseDto> bookResponseDtoList = bookService.getAll();
        log.info("authors: {}", bookResponseDtoList);
        return ResponseEntity.ok(bookResponseDtoList);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody SaveBookRequest saveBookRequest) {
        log.info("saveBookRequest: {}", saveBookRequest);
        if (saveBookRequest == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return bookService.save(saveBookRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable int id) {
        log.info("getBookById: {}", id);
        BookResponseDto bookResponseDto = bookService.getById(id);
        return ResponseEntity.ok(bookResponseDto);
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<List<BookResponseDto>> getBookByAuthorId(@PathVariable int id) {
        log.info("getBookByAuthorId: {}", id);
        List<BookResponseDto> bookResponseDto = bookService.getByAuthorId(id);
        return ResponseEntity.ok(bookResponseDto);
    }
}