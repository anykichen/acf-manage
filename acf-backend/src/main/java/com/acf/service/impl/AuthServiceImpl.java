package com.acf.service.impl;

import com.acf.common.BusinessException;
import com.acf.common.JwtUtil;
import com.acf.common.ResultCode;
import com.acf.dto.LoginDTO;
import com.acf.dto.LoginResponseDTO;
import com.acf.dto.UserInfoDTO;
import com.acf.entity.SysUser;
import com.acf.mapper.SysUserMapper;
import com.acf.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证Service实现类
 *
 * @author ACF Team
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 查询用户
        SysUser user = userMapper.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建响应
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .userType(user.getUserType())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .build();

        return LoginResponseDTO.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public void logout(String token) {
        // TODO: 实现Token黑名单或Redis清除
    }

    @Override
    public Object getCurrentUserInfo(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        return UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .userType(user.getUserType())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .build();
    }
}
