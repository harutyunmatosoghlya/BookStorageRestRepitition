package am.itspace.bookshop.repository;

import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByAuthorId(int id);
}