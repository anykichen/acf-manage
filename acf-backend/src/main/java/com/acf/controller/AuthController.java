package com.acf.controller;

import com.acf.dto.LoginDTO;
import com.acf.dto.LoginResponseDTO;
import com.acf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证Controller
 *
 * @author ACF Team
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public LoginResponseDTO login(@Validated @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user-info")
    public Object getCurrentUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.getCurrentUserInfo(token);
    }
}
