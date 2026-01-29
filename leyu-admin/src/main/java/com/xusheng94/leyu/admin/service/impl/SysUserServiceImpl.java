package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.admin.common.BizException;
import com.xusheng94.leyu.admin.model.dto.LoginDto;
import com.xusheng94.leyu.admin.model.dto.SysUserDto;
import com.xusheng94.leyu.admin.entity.SysDept;
import com.xusheng94.leyu.admin.entity.SysRole;
import com.xusheng94.leyu.admin.entity.SysUserRole;
import com.xusheng94.leyu.admin.common.enums.SourceType;
import com.xusheng94.leyu.admin.mapper.SysDeptMapper;
import com.xusheng94.leyu.admin.model.query.SysUserQuery;
import com.xusheng94.leyu.admin.service.ISysUserRoleService;
import com.xusheng94.leyu.admin.common.util.CurrentUser;
import com.xusheng94.leyu.admin.common.util.TenantQueryHelper;
import com.xusheng94.leyu.admin.model.vo.LoginVo;
import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.mapper.SysUserMapper;
import com.xusheng94.leyu.admin.service.ISysUserService;
import com.xusheng94.leyu.admin.config.security.jwt.JwtUtils;
import com.xusheng94.leyu.admin.model.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统--用户表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ISysUserRoleService sysUserRoleService;
    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<SysUser> list() {

        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        TenantQueryHelper.tenantOnly(qw);

        return super.list(qw);
    }

    @Override
    public boolean removeById(Long id) {
        SysUser sysUser;

        Long currentUserId = CurrentUser.getUserId();
        if (id.equals(currentUserId)) {
            throw new BizException(403, "不能删除当前登录用户");
        }

        sysUser = getById(id);

        if (sysUser.getSource().equals(SourceType.SYSTEM.getCode())) {
            if (CurrentUser.isTenantUser()) {
                throw new BizException(403, "系统内置用户不能删除!");
            }
        }

        super.removeById(id);
        return true;
    }

    @Override
    public SysUser getById(Long id) {
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("id", id);

        TenantQueryHelper.tenantOnly(qw);

        SysUser sysUser = super.getOne(qw);
        if (sysUser == null) {
            throw new BizException(404, "用户不存在");
        }
        return sysUser;
    }

    @Override
    public void logout() {
        Long userId = CurrentUser.getUserId();
        increaseTokenVersion(userId);
    }

    @Override
    public LoginVo login(LoginDto req) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("email", req.getEmail());
        SysUser sysUser = getOne(wrapper, false);
        if (sysUser == null || !passwordEncoder.matches(req.getPassword(), sysUser.getPassword())) {
            // 登录失败，抛业务异常
            throw new BizException(400, "邮箱或密码错误");
        }
        if (!sysUser.getStatus()) {
            throw new BizException(400, "当前用户已被禁用，请联系管理员");
        }
        // 登录成功后

        // 1️⃣ tokenVersion 自增（数据库）
        increaseTokenVersion(sysUser.getId());
        // 2️⃣ 内存同步
        sysUser.setTokenVersion(sysUser.getTokenVersion() + 1);
        // 3️⃣ 生成 token
        String token = jwtUtils.generateToken(sysUser);

        LoginVo res = new LoginVo();
        res.setToken(token);
        return res;
    }

    @Transactional
    @Override
    public Long save(SysUserDto dto) {

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(dto, sysUser);

        validateEmail(sysUser.getEmail());
        checkEmailDuplicate(sysUser.getEmail(), null);
        validatePassword(sysUser.getPassword());

        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));

        if (dto.getTenantId() == null) { // 为了平台创建租户管理员
            sysUser.setTenantId(CurrentUser.getTenantId());
        }

        if (CurrentUser.isPlatformUser() && sysUser.getTenantId() == null) {
            if (sysUser.getDeptId() != null) {
                throw new BizException(400, "平台用户不输入任何部门");
            }
        }
        super.save(sysUser);

        Long userId = sysUser.getId();
        List<Long> roleIds = dto.getRoleIds();

        saveUserRoles(userId, roleIds);

        return userId;
    }

    @Transactional
    @Override
    public boolean updateById(SysUserDto dto) {
        Long userId = dto.getId();

        // 1️⃣ 更新用户基本信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        updateById(user);

        SysUser newUser = getById(userId);

        // 不是系统创建的用户才能修改角色
        if (!newUser.getSource().equals(SourceType.SYSTEM.getCode())) {
            // 2️⃣ 删除旧角色关联
            sysUserRoleService.remove(
                    new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getUserId, userId)
            );

            // 3️⃣ 新增新角色关联
            List<Long> roleIds = dto.getRoleIds();
            saveUserRoles(userId, roleIds);
        }

        return true;
    }

    @Override
    public boolean updateById(SysUser sysUser) {
        String email = sysUser.getEmail();

        SysUser oldUser;
        try {
            oldUser = getById(sysUser.getId());
        } catch (BizException e) {
            throw new BizException(403, "用户不属于当前租户!");
        }

        // 用户创建的用户不能改系统创建的用户
        if (oldUser.getSource().equals(SourceType.SYSTEM.getCode()) ) {
            if (!CurrentUser.isSystemUser()) {
                throw new BizException(403, "无权修改!");
            }
        }

        Long currentUserId = CurrentUser.getUserId();
        if (sysUser.getId().equals(currentUserId)) {
            if (!sysUser.getStatus()) {
                throw new BizException(403, "不可禁用自己!");
            }
        }

        // TODO 允许修改邮箱的问题，目前前端是禁用修改邮箱
        // 1️⃣ 校验 email
        if (email != null) {
            validateEmail(sysUser.getEmail());
            checkEmailDuplicate(email, sysUser.getId());
        }

        // 如果密码不为空，才加密更新
        if (sysUser.getPassword() != null && !sysUser.getPassword().isBlank()) {
            validatePassword(sysUser.getPassword());
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        } else {
            sysUser.setPassword(null); // 保持原密码
        }

        return super.updateById(sysUser);
    }

    @Override
    public Page<SysUserVo> page(SysUserQuery query){

        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        TenantQueryHelper.tenantOnly(qw);

        qw.like(StringUtils.hasText(query.getName()), "name", query.getName());

        // 1️⃣ 查询用户分页
        Page<SysUser> userPage = super.page(new Page<>(query.getPage(), query.getSize()), qw);

        List<Long> userIds = userPage.getRecords().stream()
                .map(SysUser::getId)
                .collect(Collectors.toList());

        // 2️⃣ 查询用户角色（批量）
        Map<Long, List<SysRole>> userRolesMap;
        if (!userIds.isEmpty()) {
            userRolesMap = sysUserRoleService.getRolesByUserIds(userIds);
        } else {
            userRolesMap = new HashMap<>();
        }

        // 3️⃣ 查询部门（批量）
        Map<Long, SysDept> deptMap;
        if (!userIds.isEmpty()) {
            List<Long> deptIds = userPage.getRecords().stream()
                    .map(SysUser::getDeptId)
                    .filter(Objects::nonNull)
                    .distinct() // ⭐ 去重
                    .toList();
            deptMap = sysDeptMapper.selectBatchIds(deptIds).stream()
                    .collect(Collectors.toMap(SysDept::getId, Function.identity()));
        } else {
            deptMap = new HashMap<>();
        }

        // 4️⃣ 组装 DTO
        List<SysUserVo> voList = userPage.getRecords().stream().map(u -> {
            SysUserVo vo = new SysUserVo();
            BeanUtils.copyProperties(u, vo);

            // 设置角色对象列表
            vo.setRoles(userRolesMap.getOrDefault(u.getId(), Collections.emptyList()));

            // 设置角色 ID 列表
            vo.setRoleIds(
                    vo.getRoles().stream()
                            .map(SysRole::getId)
                            .collect(Collectors.toList())
            );

            SysDept dept = deptMap.get(vo.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getName());
            }

            return vo;
        }).collect(Collectors.toList());

        // 4️⃣ 返回分页
        Page<SysUserVo> voPage = new Page<>();
        BeanUtils.copyProperties(userPage, voPage, "records");
        voPage.setRecords(voList);

        return voPage;
    }


    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BizException(400, "邮箱不能为空");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BizException(400, "邮箱格式不正确");
        }
    }

    private void checkEmailDuplicate(String email, Long excludeId) {
        boolean exists = this.lambdaQuery()
                .eq(SysUser::getEmail, email)
                .ne(excludeId != null, SysUser::getId, excludeId)
                .exists();
        if (exists) {
            throw new BizException(400, "该邮箱已被注册");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BizException(400, "密码不能为空");
        }
        if (password.length() < 6) {
            throw new BizException(400, "密码长度不能少于6位");
        }
    }

    public void increaseTokenVersion(Long userId) {
        baseMapper.update(
                null,
                new UpdateWrapper<SysUser>()
                        .eq("id", userId)
                        .setSql("token_version = token_version + 1")
        );
    }

    /**
     * 给用户批量更新角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> list = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setRoleId(roleId);
                        return sysUserRole;
                    })
                    .collect(Collectors.toList());
            sysUserRoleService.saveBatch(list);
        }
    }
}
