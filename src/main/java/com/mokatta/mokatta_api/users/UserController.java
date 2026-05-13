package com.mokatta.mokatta_api.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mokatta.mokatta_api.users.dtos.CreateUserRequest;
import com.mokatta.mokatta_api.users.dtos.UpdateUserRequest;
import com.mokatta.mokatta_api.users.dtos.UserResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management (requires JWT)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users", description = "Returns all registered users.", responses = {
            @ApiResponse(responseCode = "200", description = "User list")
    })
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", responses = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Registers a new user in the system.", responses = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate email", content = @Content)
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates the user's name and/or password.", responses = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle user active status",
            description = "Toggles the user's active/inactive status. Inactive users cannot log in.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Status changed"),
                    @ApiResponse(responseCode = "400", description = "User not found", content = @Content)
            })
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        userService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }
}
