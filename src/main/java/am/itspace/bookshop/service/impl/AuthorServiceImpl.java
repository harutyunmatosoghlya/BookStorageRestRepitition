package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.mapper.AuthorMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.service.AuthorService;
import am.itspace.bookshop.util.ValueUpdateUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final ValueUpdateUtil valueUpdateUtil;

    @Override
    public List<AuthorResponseDto> getAll() {
        List<Author> authorList = authorRepository.findAll();
        return authorMapper.toDtoList(authorList);
    }

    @Override
    public AuthorResponseDto getById(int id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.map(authorMapper::toDto).orElse(null);
    }

    @Override
    public ResponseEntity<AuthorResponseDto> save(SaveAuthorRequest saveAuthorRequest) {
        List<Author> authorList = authorRepository.findAll();
        Author author = authorMapper.toEntity(saveAuthorRequest);
        if (authorList.stream().anyMatch(author::equals)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(authorMapper.toDto(authorRepository.save(author)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthorResponseDto> update(SaveAuthorRequest saveAuthorRequest, int id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            Author updatedAuthor = updateAuthorFields(author.get(), saveAuthorRequest);
            return new ResponseEntity<>(authorMapper.toDto(authorRepository.save(updatedAuthor)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public void delete(int id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
    }

    private Author updateAuthorFields(Author author, SaveAuthorRequest saveAuthorRequest) {
        author.setName(valueUpdateUtil.getOrDefault(saveAuthorRequest.getName(), author.getName()));
        author.setSurname(valueUpdateUtil.getOrDefault(saveAuthorRequest.getSurname(), author.getSurname()));
        author.setPhone(valueUpdateUtil.getOrDefault(saveAuthorRequest.getPhone(), author.getPhone()));
        author.setDateOfBirthday(valueUpdateUtil.getOrDefault(saveAuthorRequest.getDateOfBirthday(), author.getDateOfBirthday()));
        author.setGender(valueUpdateUtil.getOrDefault(saveAuthorRequest.getGender(), author.getGender()));
        return author;
    }
}