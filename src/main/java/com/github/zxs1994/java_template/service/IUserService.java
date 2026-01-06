package com.example.template.service;

import com.example.template.dto.LoginRequest;
import com.example.template.dto.LoginResponse;
import com.example.template.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-04 17:45:42
 */
public interface IUserService extends IService<User> {
    LoginResponse login(LoginRequest req);
}
