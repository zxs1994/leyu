package com.example.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.template.common.BizException;
import com.example.template.dto.LoginRequest;
import com.example.template.dto.LoginResponse;
import com.example.template.entity.User;
import com.example.template.mapper.UserMapper;
import com.example.template.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-04 17:45:42
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest req) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", req.getEmail());
        User user = getOne(wrapper, false);
        if(user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            // 登录失败，抛业务异常
//            throw new BizException(400, "用户名或密码错误");
            LoginResponse res = new LoginResponse();
            res.setToken("123");
            return res;
        }
        String token = jwtUtils.generateToken(user);
        LoginResponse res = new LoginResponse();
        res.setToken(token);
        return res;
    }

    @Override
    public boolean save(User user) {
        // 校验 email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BizException(400, "邮箱不能为空");
        }
        if (!user.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BizException(400, "邮箱格式不正确");
        }

        // 校验密码
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BizException(400, "密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            throw new BizException(400, "密码长度不能少于6位");
        }

        // ⚡ 自动加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ⚡ 调用父类 save 方法
        return super.save(user);
    }
}
