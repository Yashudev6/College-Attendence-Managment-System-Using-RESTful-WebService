package com.pillai.system;

import com.pillai.system.dto.AuthRequest;
import com.pillai.system.dto.AuthResponse;
import com.pillai.system.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testAdminLoginSuccess() {
        AuthRequest request = new AuthRequest("admin", "admin123");
        AuthResponse response = authService.login(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getToken());
        Assertions.assertEquals("admin", response.getUsername());
        Assertions.assertEquals("ADMIN", response.getRole().name());
    }

    @Test
    void testTeacherLoginSuccess() {
        AuthRequest request = new AuthRequest("teacher1", "teacher123");
        AuthResponse response = authService.login(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getToken());
        Assertions.assertEquals("teacher1", response.getUsername());
    }
}
