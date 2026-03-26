package com.acf.service;

import com.acf.dto.LoginDTO;
import com.acf.dto.LoginResponseDTO;

/**
 * 认证Service
 *
 * @author ACF Team
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     */
    Object getCurrentUserInfo(String token);
}
