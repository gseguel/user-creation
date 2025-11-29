package com.user.dto;

import com.user.util.message.UserMessage;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {
    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String name;

    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String email;

    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String password;

    @NotNull(message = UserMessage.FIELD_REQUIRED)
    private List<PhoneDto> phones;

    private boolean isActive = true;
}
