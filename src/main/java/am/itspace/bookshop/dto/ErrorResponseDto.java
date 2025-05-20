package am.itspace.bookshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {
    private String message;
    private String status;
    private int statusCode;
}