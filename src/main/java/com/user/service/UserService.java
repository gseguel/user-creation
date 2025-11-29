package com.user.service;

import com.user.dto.UserRequestDto;
import com.user.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    UserResponseDto register(UserRequestDto req);
    List<UserResponseDto> listAll();
    UserResponseDto getById(String id);
    UserResponseDto update(String id, UserRequestDto req);
    void delete(String id);
}
