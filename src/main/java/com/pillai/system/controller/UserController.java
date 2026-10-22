package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.UserDto;
import com.pillai.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("User list retrieved successfully", users));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Boolean> payload) {
        boolean enabled = payload.getOrDefault("enabled", true);
        UserDto updated = userService.updateUserStatus(id, enabled);
        return ResponseEntity.ok(ApiResponse.success("User status updated to " + (enabled ? "enabled" : "disabled"), updated));
    }
}
