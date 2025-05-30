package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.entity.Gender;
import am.itspace.bookshop.mapper.AuthorMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.util.ValueUpdateUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private AuthorMapper authorMapper;
    @Mock
    private ValueUpdateUtil valueUpdateUtil;
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void testGetAll() {
        List<Author> authors = List.of(
                Author.builder().id(1).name("example").surname("exampleyan").build()
        );
        List<AuthorResponseDto> dtos = List.of(
                AuthorResponseDto.builder().id(1).name("example").surname("exampleyan").build()
        );
        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.toDtoList(authors)).thenReturn(dtos);
        List<AuthorResponseDto> result = authorService.getAll();
        assertThat(result).isEqualTo(dtos);
    }

    @Test
    void testGetByIdFound() {
        Author author = Author.builder().id(1).name("example").build();
        AuthorResponseDto dto = AuthorResponseDto.builder().id(1).name("example").build();
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(dto);
        AuthorResponseDto result = authorService.getById(1);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testGetByIdNotFound() {
        when(authorRepository.findById(1)).thenReturn(Optional.empty());
        AuthorResponseDto result = authorService.getById(1);
        assertThat(result).isNull();
    }

    @Test
    void testSaveNewAuthor() {
        SaveAuthorRequest request = SaveAuthorRequest.builder()
                .name("example").surname("exampleyan2").phone("123").gender(Gender.MALE).build();
        Author authorEntity = Author.builder().name("example").surname("exampleyan2").build();
        Author savedAuthor = Author.builder().id(1).name("example").surname("exampleyan2").build();
        AuthorResponseDto responseDto = AuthorResponseDto.builder().id(1).name("example").surname("exampleyan2").build();
        when(authorRepository.findAll()).thenReturn(Collections.emptyList());
        when(authorMapper.toEntity(request)).thenReturn(authorEntity);
        when(authorRepository.save(authorEntity)).thenReturn(savedAuthor);
        when(authorMapper.toDto(savedAuthor)).thenReturn(responseDto);
        ResponseEntity<AuthorResponseDto> result = authorService.save(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(responseDto);
    }

    @Test
    void testSaveDuplicateAuthor() {
        SaveAuthorRequest request = SaveAuthorRequest.builder().name("example").build();
        Author existingAuthor = Author.builder().name("example").build();
        when(authorRepository.findAll()).thenReturn(List.of(existingAuthor));
        when(authorMapper.toEntity(request)).thenReturn(existingAuthor);
        ResponseEntity<AuthorResponseDto> result = authorService.save(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateFound() {
        SaveAuthorRequest request = SaveAuthorRequest.builder().name("new").build();
        Author existing = Author.builder().id(1).name("old").build();
        Author updated = Author.builder().id(1).name("new").build();
        AuthorResponseDto dto = AuthorResponseDto.builder().id(1).name("new").build();
        when(authorRepository.findById(1)).thenReturn(Optional.of(existing));
        when(valueUpdateUtil.getOrDefault("old", "new")).thenReturn("new");
        when(authorRepository.save(any())).thenReturn(updated);
        when(authorMapper.toDto(updated)).thenReturn(dto);
        ResponseEntity<AuthorResponseDto> result = authorService.update(request, 1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(dto);
    }

    @Test
    void testUpdateNotFound() {
        SaveAuthorRequest request = SaveAuthorRequest.builder().name("new").build();
        when(authorRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<AuthorResponseDto> result = authorService.update(request, 1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteSuccess() {
        when(authorRepository.existsById(1)).thenReturn(true);
        authorService.delete(1);
        verify(authorRepository, times(1)).deleteById(1);
    }


    @Test
    void testDeleteNotFound() {
        when(authorRepository.existsById(1)).thenReturn(false);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> authorService.delete(1));
        assertThat(thrown.getMessage()).contains("Author with id 1 not found");
    }
}