package am.itspace.bookshop.service;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookService {
    List<BookResponseDto> getAll();

    BookResponseDto getById(int id);

    ResponseEntity<BookResponseDto> save(SaveBookRequest saveBookRequest);

    List<BookResponseDto> getByAuthorId(int authorId);

    ResponseEntity<BookResponseDto> update(SaveBookRequest saveBookRequest, int id);

    void delete(int id);
}