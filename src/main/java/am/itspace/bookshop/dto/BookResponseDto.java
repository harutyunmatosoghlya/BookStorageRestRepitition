package am.itspace.bookshop.dto;

import am.itspace.bookshop.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {
    private int id;
    private String title;
    private double price;
    private Author author;
}