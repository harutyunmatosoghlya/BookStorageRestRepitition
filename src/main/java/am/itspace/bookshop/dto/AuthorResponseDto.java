package am.itspace.bookshop.dto;

import am.itspace.bookshop.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponseDto {
    private int id;
    private String name;
    private String surname;
    private String phone;
    private Gender gender;
}