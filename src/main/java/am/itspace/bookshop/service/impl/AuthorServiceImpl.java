package am.itspace.bookshop.service.impl;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Author;
import am.itspace.bookshop.mapper.AuthorMapper;
import am.itspace.bookshop.repository.AuthorRepository;
import am.itspace.bookshop.service.AuthorService;
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
}