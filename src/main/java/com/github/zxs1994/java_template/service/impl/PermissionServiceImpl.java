package com.example.template.service.impl;

import com.example.template.entity.Permission;
import com.example.template.mapper.PermissionMapper;
import com.example.template.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-04 17:32:23
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
