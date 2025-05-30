package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.entity.Book;
import am.itspace.bookshop.exaption.BookNotFoundException;
import am.itspace.bookshop.mapper.BookMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.repository.BookRepository;
import am.itspace.bookshop.service.BookService;
import am.itspace.bookshop.util.ValueUpdateUtil;
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
    private final ValueUpdateUtil valueUpdateUtil;

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
            throw new BookNotFoundException("Book with id " + id + " not found");
        }
        bookRepository.deleteById(id);
    }

    private Book updateBookFields(Book book, SaveBookRequest saveBookRequest) {
        book.setTitle(valueUpdateUtil.getOrDefault(book.getTitle(), saveBookRequest.getTitle()));
        book.setPrice(valueUpdateUtil.getOrDefault(book.getPrice(), saveBookRequest.getPrice()));
        book.setQty(valueUpdateUtil.getOrDefault(book.getQty(), saveBookRequest.getQty()));
        Author author = authorRepository.findById(saveBookRequest.getAuthorId())
                .orElseThrow(() -> new BookNotFoundException("Author with id " + saveBookRequest.getAuthorId() + " not found"));
        book.setAuthor(author);
        return book;
    }
}