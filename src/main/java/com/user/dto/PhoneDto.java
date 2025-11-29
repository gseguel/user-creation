package com.user.dto;

import com.user.util.message.UserMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto {
    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String number;
    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String cityCode;
    @NotBlank(message = UserMessage.FIELD_REQUIRED)
    private String countryCode;
}