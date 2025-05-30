package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.entity.Book;
import am.itspace.bookshop.mapper.BookMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.repository.BookRepository;
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
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private ValueUpdateUtil valueUpdateUtil;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testGetAll() {
        List<Book> books = List.of(new Book());
        List<BookResponseDto> dtos = List.of(new BookResponseDto());
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDtoList(books)).thenReturn(dtos);
        List<BookResponseDto> result = bookService.getAll();
        assertThat(result).isEqualTo(dtos);
    }

    @Test
    void testGetByIdFound() {
        Book book = new Book();
        BookResponseDto dto = new BookResponseDto();
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);
        BookResponseDto result = bookService.getById(1);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testGetByIdNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());
        BookResponseDto result = bookService.getById(1);
        assertThat(result).isNull();
    }

    @Test
    void testSaveNewBook() {
        SaveBookRequest request = SaveBookRequest.builder().title("Test").price(10).qty(1).authorId(1).build();
        Book book = new Book();
        Book saved = new Book();
        BookResponseDto dto = new BookResponseDto();
        Author author = new Author();
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(saved);
        when(bookMapper.toDto(saved)).thenReturn(dto);
        ResponseEntity<BookResponseDto> result = bookService.save(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(dto);
    }

    @Test
    void testSaveDuplicateBookAddsQty() {
        Book duplicate = Book.builder().title("Test").price(10).qty(1).build();
        SaveBookRequest request = SaveBookRequest.builder().title("Test").price(10).qty(1).authorId(1).build();
        Author author = new Author();
        when(bookRepository.findAll()).thenReturn(List.of(duplicate));
        when(bookMapper.toEntity(request)).thenReturn(duplicate);
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(bookRepository.save(duplicate)).thenReturn(duplicate);
        when(bookMapper.toDto(duplicate)).thenReturn(new BookResponseDto());
        ResponseEntity<BookResponseDto> result = bookService.save(request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateFound() {
        Book oldBook = Book.builder().title("Old").price(5).qty(1).build();
        SaveBookRequest request = SaveBookRequest.builder().title("New").price(10).qty(2).authorId(1).build();
        Book updated = Book.builder().title("New").price(10).qty(2).build();
        BookResponseDto dto = new BookResponseDto();
        Author author = new Author();
        when(bookRepository.findById(1)).thenReturn(Optional.of(oldBook));
        when(valueUpdateUtil.getOrDefault("Old", "New")).thenReturn("New");
        when(valueUpdateUtil.getOrDefault(5.0, 10.0)).thenReturn(10.0);
        when(valueUpdateUtil.getOrDefault(1, 2)).thenReturn(2);
        when(authorRepository.findById(1)).thenReturn(Optional.of(author)); // 🔥 важно!
        when(bookRepository.save(oldBook)).thenReturn(updated);
        when(bookMapper.toDto(updated)).thenReturn(dto);
        ResponseEntity<BookResponseDto> result = bookService.update(request, 1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(dto);
    }

    @Test
    void testUpdateNull() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<BookResponseDto> result = bookService.update(null, 1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteSuccess() {
        when(bookRepository.existsById(1)).thenReturn(true);
        bookService.delete(1);
        verify(bookRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound() {
        when(bookRepository.existsById(1)).thenReturn(false);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> bookService.delete(1));
        assertThat(thrown.getMessage()).contains("Book with id 1 not found");
    }

    @Test
    void testGetByAuthorId() {
        List<Book> books = List.of(new Book());
        List<BookResponseDto> dtos = List.of(new BookResponseDto());
        when(bookRepository.findByAuthorId(1)).thenReturn(books);
        when(bookMapper.toDto(any())).thenReturn(dtos.get(0));
        List<BookResponseDto> result = bookService.getByAuthorId(1);
        assertThat(result).isEqualTo(dtos);
    }
}