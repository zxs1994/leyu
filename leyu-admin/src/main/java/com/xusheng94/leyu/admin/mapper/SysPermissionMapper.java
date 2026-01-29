package com.xusheng94.leyu.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xusheng94.leyu.admin.entity.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统--权限表 Mapper 接口
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 查询用户拥有的权限
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("""
        SELECT p.*
        FROM sys__permission p
        JOIN sys__role_permission rp ON rp.permission_id = p.id
        JOIN sys__user_role ur ON ur.role_id = rp.role_id
        WHERE ur.user_id = #{userId}
          AND p.status = 1
        """)
    List<SysPermission> selectByUserId(@Param("userId") Long userId);
}
