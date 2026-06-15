package edu.univ.erp.controller;

import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApi authApi;
    private final JwtUtil jwtUtil;

    public AuthController(AuthApi authApi, JwtUtil jwtUtil) {
        this.authApi = authApi;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        LoginResult result = authApi.attemptLogin(username, password);

        if (result.getStatus() == LoginStatus.SUCCESS) {
            String token = jwtUtil.generateToken(result.getUserId(), result.getRole());
            return ResponseEntity.ok(Map.of("token", token, "role", result.getRole()));
        }
        return ResponseEntity.status(401).body(Map.of("status", result.getStatus().name()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal Integer userId,
                                            @RequestBody Map<String, String> body) {
        boolean ok = authApi.changePassword(userId, body.get("currentPassword"), body.get("newPassword"));
        return ok ? ResponseEntity.ok().build() : ResponseEntity.status(400).body(Map.of("error", "Password change failed"));
    }
}
