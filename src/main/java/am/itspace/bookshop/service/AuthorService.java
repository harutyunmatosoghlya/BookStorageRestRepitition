package am.itspace.bookshop.service;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthorService {
    List<AuthorResponseDto> getAll();

    AuthorResponseDto getById(int id);

    ResponseEntity<AuthorResponseDto> save(SaveAuthorRequest saveAuthorRequest);
}