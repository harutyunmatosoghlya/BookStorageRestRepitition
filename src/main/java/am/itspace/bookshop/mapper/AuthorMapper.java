package am.itspace.bookshop.mapper;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Author;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponseDto toDto(Author author);

    List<AuthorResponseDto> toDtoList(List<Author> authors);

    Author toEntity(SaveAuthorRequest authorRequest);
}