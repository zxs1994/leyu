package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xusheng94.leyu.admin.config.security.LoginUser;
import com.xusheng94.leyu.admin.config.security.jwt.JwtUtils;
import com.xusheng94.leyu.admin.model.dto.SysUserDto;
import com.xusheng94.leyu.admin.model.dto.TenantDto;
import com.xusheng94.leyu.admin.entity.SysDept;
import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.common.enums.SourceType;
import com.xusheng94.leyu.admin.mapper.SysDeptMapper;
import com.xusheng94.leyu.admin.service.IPlatformTenantService;
import com.xusheng94.leyu.admin.service.ISysDeptService;
import com.xusheng94.leyu.admin.service.ISysUserService;
import com.xusheng94.leyu.admin.common.util.CurrentUser;
import com.xusheng94.leyu.admin.model.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlatformTenantServiceImpl implements IPlatformTenantService {

    private final JwtUtils jwtUtils;
    private final SysDeptMapper sysDeptMapper;
    private final ISysUserService sysUserService;
    private final ISysDeptService sysDeptService;

    @Override
    @Transactional
    public Long addTenantWithAdmin(TenantDto dto) {

        // 1️⃣ 生成雪花ID
        Long tenantId = IdWorker.getId();
        Long adminUserId = IdWorker.getId();

        // 2️⃣ 创建顶级租户（部门）
        SysDept tenant = new SysDept();
        tenant.setId(tenantId);
        tenant.setName(dto.getName());
        tenant.setParentId(null);               // 顶级部门
        tenant.setPath("/" + tenantId);
        tenant.setTenantId(tenantId);          // 顶级租户的 tenantId 是自己
        tenant.setLeaderId(adminUserId);
        sysDeptMapper.insert(tenant);

        // 3️⃣ 创建默认管理员
        SysUserDto admin = dto.getAdminUser();
        admin.setId(adminUserId);
        admin.setTenantId(tenantId);
        admin.setDeptId(tenantId);
        admin.setSource(SourceType.SYSTEM.getCode());    // 系统创建
        admin.setRoleIds(List.of(1L)); // 超级管理员
        sysUserService.save(admin);

        return tenantId;
    }

    @Override
    public List<SysDept> list() {
        // 只返回 parentId 为 null 的顶级部门（即企业/租户）
        return sysDeptService.list(
                Wrappers.<SysDept>lambdaQuery()
                        .isNull(SysDept::getParentId)
        );
    }

    @Override
    public LoginVo switchTo(Long tenantId) {

        SysUser sysUser = new SysUser();
        LoginUser loginUser = CurrentUser.getLoginUser();

        BeanUtils.copyProperties(Objects.requireNonNull(loginUser), sysUser);
        sysUser.setTenantId(tenantId);
        sysUser.setId(loginUser.getUserId());

        String token = jwtUtils.generateToken(sysUser);

        LoginVo res = new LoginVo();
        res.setToken(token);

        return res;
    }
}
