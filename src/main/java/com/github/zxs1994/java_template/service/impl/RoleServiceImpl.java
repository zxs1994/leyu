package com.example.template.service.impl;

import com.example.template.entity.Role;
import com.example.template.mapper.RoleMapper;
import com.example.template.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-04 17:32:23
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
