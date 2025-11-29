package com.user.service;

import com.user.dto.UserRequestDto;
import com.user.dto.UserResponseDto;
import com.user.entity.PhoneEntity;
import com.user.entity.UserEntity;
import com.user.exception.EmailAlreadyExistsException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.user.util.message.UserMessage.*;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repo;
    private final JwtProvider jwtProvider;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository repo, JwtProvider jwtProvider,
                           @Value("${validation.email.regex}") String emailRegex,
                           @Value("${validation.password.regex}") String passwordRegex) {
        this.repo = repo;
        this.jwtProvider = jwtProvider;
        this.emailPattern = Pattern.compile(emailRegex);
        this.passwordPattern = Pattern.compile(passwordRegex);
    }

    @Transactional
    @Override
    public UserResponseDto register(UserRequestDto req) {
        String email = req.getEmail().toLowerCase().trim();
        if (!emailPattern.matcher(email).matches()) {
            throw new IllegalArgumentException(INVALID_EMAIL);
        }
        if (!passwordPattern.matcher(req.getPassword()).matches()) {
            throw new IllegalArgumentException(INVALID_PASSWORD );
        }
        if (repo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS );
        }
        UserEntity u = UserMapper.toDtoToEntity(req);
        u.setPassword(encoder.encode(req.getPassword()));
        LocalDateTime now = LocalDateTime.now();
        u.setCreated(now);
        u.setModified(now);
        u.setLastLogin(now);
        String token = jwtProvider.generateToken(u.getEmail());
        u.setToken(token);
        UserEntity saved = repo.save(u);
        return UserMapper.toEntityToDto(saved);
    }

    @Override
    public List<UserResponseDto> listAll() {
        return repo.findAll().stream().map(UserMapper::toEntityToDto).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getById(String id) {
        return repo.findById(id).map(UserMapper::toEntityToDto).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public UserResponseDto update(String id, UserRequestDto req) {
        UserEntity u = repo.findById(id).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
        u.setName(req.getName());
        String email = req.getEmail().toLowerCase().trim();
        if (!emailPattern.matcher(email).matches()) throw new IllegalArgumentException(INVALID_EMAIL);
        if (!email.equals(u.getEmail()) && repo.existsByEmail(email)) throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);
        u.setEmail(email);
        if (!passwordPattern.matcher(req.getPassword()).matches()) throw new IllegalArgumentException(INVALID_PASSWORD);
        u.setPassword(encoder.encode(req.getPassword()));
        if (req.getPhones() != null) {
            List<PhoneEntity> existingPhones = u.getPhones();
            existingPhones.clear();
            existingPhones.addAll(UserMapper.convertPhonesDtoToEntities(req.getPhones()));
        }
        u.setModified(LocalDateTime.now());
        u.setActive(req.isActive());
        UserEntity saved = repo.save(u);
        return UserMapper.toEntityToDto(saved);
    }

    @Transactional
    @Override
    public void delete(String id) {
        UserEntity u = repo.findById(id).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
        repo.delete(u);
    }
}
