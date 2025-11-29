package com.user.dto;

import com.user.util.message.UserMessage;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
