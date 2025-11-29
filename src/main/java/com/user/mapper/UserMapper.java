package com.user.mapper;

import com.user.dto.PhoneDto;
import com.user.dto.UserRequestDto;
import com.user.dto.UserResponseDto;
import com.user.entity.PhoneEntity;
import com.user.entity.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static UserResponseDto toEntityToDto(UserEntity userEntity){
        List<PhoneDto> phones = userEntity.getPhones()
                .stream()
                .map(p -> PhoneDto.builder()
                        .number(p.getNumber())
                        .cityCode(p.getCityCode())
                        .countryCode(p.getCountryCode())
                        .build())
                .collect(Collectors.toList());

        return UserResponseDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phones(phones)
                .created(userEntity.getCreated())
                .modified(userEntity.getModified())
                .last_login(userEntity.getLastLogin())
                .token(userEntity.getToken())
                .isActive(userEntity.isActive())
                .build();
    }

    public static UserEntity toDtoToEntity(UserRequestDto userRequestDto) {
        if (userRequestDto == null) return null;
        return UserEntity.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .phones(convertPhonesDtoToEntities(userRequestDto.getPhones()))
                .build();
    }

    public static List<PhoneEntity> convertPhonesDtoToEntities(List<PhoneDto> phoneDtoList) {
        if (phoneDtoList == null) return new ArrayList<>();
        return phoneDtoList.stream()
                .map(dto -> PhoneEntity.builder()
                        .number(dto.getNumber())
                        .cityCode(dto.getCityCode())
                        .countryCode(dto.getCountryCode())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
