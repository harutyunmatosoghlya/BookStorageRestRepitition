package am.itspace.bookshop.dto;

import am.itspace.bookshop.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserRequest {
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserType userType;
}