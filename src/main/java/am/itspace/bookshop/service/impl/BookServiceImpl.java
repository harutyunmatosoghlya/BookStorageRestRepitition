package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.entity.Book;
import am.itspace.bookshop.mapper.BookMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.repository.BookRepository;
import am.itspace.bookshop.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;

    @Override
    public List<BookResponseDto> getAll() {
        List<Book> authorList = bookRepository.findAll();
        return bookMapper.toDtoList(authorList);
    }

    @Override
    public BookResponseDto getById(int id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(bookMapper::toDto).orElse(null);
    }

    @Override
    public ResponseEntity<BookResponseDto> save(SaveBookRequest saveBookRequest) {
        List<Book> bookList = bookRepository.findAll();
        Book book = bookMapper.toEntity(saveBookRequest);
        if (bookList.stream().anyMatch(book::equals)) {
            book.setQty(book.getQty() + saveBookRequest.getQty());
        }
        book.setAuthor(authorRepository.findById(saveBookRequest.getAuthorId()).orElse(null));
        return new ResponseEntity<>(bookMapper.toDto(bookRepository.save(book)), HttpStatus.CREATED);
    }

    @Override
    public List<BookResponseDto> getByAuthorId(int authorId) {
        List<Book> books = bookRepository.findByAuthorId(authorId);
        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public ResponseEntity<BookResponseDto> update(SaveBookRequest saveBookRequest, int id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Book updatedBook = updateBookFields(book.get(), saveBookRequest);
            return new ResponseEntity<>(bookMapper.toDto(bookRepository.save(updatedBook)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void delete(int id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id " + id + " not found");
        }
        bookRepository.deleteById(id);
    }

    private Book updateBookFields(Book book, SaveBookRequest saveBookRequest) {
        book.setTitle(getOrDefault(book.getTitle(), saveBookRequest.getTitle()));
        book.setPrice(getOrDefault(book.getPrice(), saveBookRequest.getPrice()));
        book.setQty(getOrDefault(book.getQty(), saveBookRequest.getQty()));
        if (book.getAuthor() == null && authorRepository.findById(saveBookRequest.getAuthorId()).isPresent()) {
            Author author = authorRepository.findById(saveBookRequest.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Author with id " + saveBookRequest.getAuthorId() + " not found"));
            book.setAuthor(author);
        }
        return book;
    }


    private <T> T getOrDefault(T current, T incoming) {
        if (incoming == null) return current;
        if (incoming instanceof String && ((String) incoming).trim().isEmpty()) return current;
        return incoming;
    }
}