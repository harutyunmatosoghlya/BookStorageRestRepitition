package am.itspace.bookshop.mapper;

import am.itspace.bookshop.dto.BookResponseDto;
import am.itspace.bookshop.dto.SaveBookRequest;
import am.itspace.bookshop.entity.Book;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface BookMapper {
    BookResponseDto toDto(Book book);
    List<BookResponseDto> toDtoList(List<Book> books);
    Book toEntity(SaveBookRequest bookRequest);
}