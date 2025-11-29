package com.user.controller;

import com.user.dto.UserRequestDto;
import com.user.dto.UserResponseDto;
import com.user.service.UserService;
import com.user.util.message.UserMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
@Tag(name = "Users", description = "Operations related to user management with JWT authentication")
public class UserController {

    private final UserService service;
    public UserController(UserService service){ this.service = service; }

    @Operation(summary = "Register a new user", description = "Creates a new user in the system and returns its data along with a JWT token")
    @PostMapping(value = "/user/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRequestDto req){
        UserResponseDto created = service.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "List all users", description = "Returns a list of all registered users")
    @GetMapping(value= "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseDto>> list(){
        return ResponseEntity.ok(service.listAll());
    }

    @Operation(summary = "Get user by ID", description = "Returns the data of a specific user by their ID")
    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDto> get(
            @PathVariable String id){
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Update a user", description = "Updates the data of an existing user")
    @PutMapping(value = "/user/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable String id,
            @Valid @RequestBody UserRequestDto req){
        return ResponseEntity.ok(service.update(id, req));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @DeleteMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> delete(@PathVariable String id){
        service.delete(id);
        return ResponseEntity.ok(Map.of(UserMessage.MESSAGE, UserMessage.USER_DELETED));
    }
}