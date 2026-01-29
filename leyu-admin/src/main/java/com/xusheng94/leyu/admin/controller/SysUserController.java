package com.xusheng94.leyu.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xusheng94.leyu.admin.common.BizException;
import com.xusheng94.leyu.admin.model.dto.SysUserDto;
import com.xusheng94.leyu.admin.model.dto.UserInfoDto;
import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.mapper.SysUserMapper;
import com.xusheng94.leyu.admin.model.query.SysUserQuery;
import com.xusheng94.leyu.admin.service.ISysPermissionService;
import com.xusheng94.leyu.admin.service.ISysUserService;
import com.xusheng94.leyu.admin.common.util.CurrentUser;
import com.xusheng94.leyu.admin.model.vo.SysUserInfoVo;
import com.xusheng94.leyu.admin.model.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 系统--用户表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
@Tag(name = "系统--用户", description = "系统--用户控制器")
public class SysUserController {

    private final ISysUserService sysUserService;
    private final ISysPermissionService sysPermissionService;
    private final SysUserMapper sysUserMapper;

    @GetMapping("/page")
    @Operation(summary = "用户列表(分页)")
    public Page<SysUserVo> page(SysUserQuery query) {
        return sysUserService.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户")
    public SysUser item(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增用户")
    public Long add(@RequestBody SysUserDto sysUserDto) {
        Long id = sysUserService.save(sysUserDto);
        if (id == null) {
            throw new BizException(400, "新增用户失败");
        }
        return id;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public void update(@PathVariable Long id, @RequestBody SysUserDto sysUserDto) {
        sysUserDto.setId(id);
        boolean success = sysUserService.updateById(sysUserDto);
        if (!success) {
            throw new BizException(400, "更新用户失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void delete(@PathVariable Long id) {
        boolean success = sysUserService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除用户失败");
        }
    }

    @GetMapping
    @Operation(summary = "用户列表")
    public List<SysUser> list() {
        return sysUserService.list();
    }

    @GetMapping("/info")
    @Operation(summary = "获取登录用户信息")
    public SysUserInfoVo getUserInfo() {
        Long userId = CurrentUser.getUserId();
        // 为了平台用户可以切换租户 不使用sysUserService.getById
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("id", userId);
        SysUser sysUser = sysUserService.getOne(qw);
        List<String> list = sysPermissionService.getCodesByUserId(userId);
        return new SysUserInfoVo(sysUser, list);
    }

    @PutMapping("/info")
    @Operation(summary = "修改登录用户信息")
    public void updateUserInfo(@RequestBody UserInfoDto userInfo) {
        Long userId = CurrentUser.getUserId();
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setName(userInfo.getName());
        // 为了平台用户可以切换租户 不使用sysUserService.updateById
        int count = sysUserMapper.updateById(sysUser);
        if (count != 1) {
            throw new BizException(400, "更新用户失败");
        }
    }

}