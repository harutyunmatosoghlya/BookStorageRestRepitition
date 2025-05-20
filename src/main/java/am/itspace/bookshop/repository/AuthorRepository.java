package am.itspace.bookshop.repository;

import am.itspace.bookshop.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findByPhone(String phone);
}