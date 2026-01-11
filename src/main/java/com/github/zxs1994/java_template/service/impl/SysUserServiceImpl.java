package com.github.zxs1994.java_template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.dto.LoginRequest;
import com.github.zxs1994.java_template.dto.LoginResponse;
import com.github.zxs1994.java_template.entity.SysUser;
import com.github.zxs1994.java_template.mapper.SysUserMapper;
import com.github.zxs1994.java_template.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zxs1994.java_template.config.jwt.JwtUtils;
import com.github.zxs1994.java_template.service.SystemProtectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统--用户表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
public class SysUserServiceImpl extends SystemProtectService<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserMapper sysUserMapper;

    public LoginResponse login(LoginRequest req) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("email", req.getEmail());
        SysUser sysUser = getOne(wrapper, false);
        if(sysUser == null || !passwordEncoder.matches(req.getPassword(), sysUser.getPassword())) {
            // 登录失败，抛业务异常
            throw new BizException(400, "用户名或密码错误");
        }
        // 登录成功后
        SysUser newUser = new SysUser();
        newUser.setId(sysUser.getId());
        newUser.setTokenVersion(sysUser.getTokenVersion() + 1);
        sysUserMapper.updateById(newUser);

        String token = jwtUtils.generateToken(newUser);
        LoginResponse res = new LoginResponse();
        res.setToken(token);
        return res;
    }

    @Override
    public boolean save(SysUser sysUser) {
        // 1️⃣ 校验 email
        if (sysUser.getEmail() == null || sysUser.getEmail().isBlank()) {
            throw new BizException(400, "邮箱不能为空");
        }
        if (!sysUser.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BizException(400, "邮箱格式不正确");
        }

        // 2️⃣ 校验 email 是否重复
        boolean exists = this.lambdaQuery()
                .eq(SysUser::getEmail, sysUser.getEmail())
                .exists();

        if (exists) {
            throw new BizException(400, "该邮箱已被注册");
        }

        // 3️⃣ 校验密码
        if (sysUser.getPassword() == null || sysUser.getPassword().isBlank()) {
            throw new BizException(400, "密码不能为空");
        }
        if (sysUser.getPassword().length() < 6) {
            throw new BizException(400, "密码长度不能少于6位");
        }

        // 4️⃣ 加密密码
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));

        // 5️⃣ 保存
        return super.save(sysUser);
    }
}
