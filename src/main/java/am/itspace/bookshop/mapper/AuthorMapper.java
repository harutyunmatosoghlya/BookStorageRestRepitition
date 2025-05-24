package am.itspace.bookshop.mapper;

import am.itspace.bookshop.dto.AuthorResponseDto;
import am.itspace.bookshop.dto.SaveAuthorRequest;
import am.itspace.bookshop.entity.Author;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface AuthorMapper {
    AuthorResponseDto toDto(Author author);

    List<AuthorResponseDto> toDtoList(List<Author> authors);

    Author toEntity(SaveAuthorRequest authorRequest);
}