package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.model.dto.SysRoleDto;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleVo extends SysRoleDto {

    @Schema(description = "权限 list")
    List<SysPermission> permissions;

    public SysRoleVo(SysRole sysRole, List<SysPermission> permissions) {
        // 拷贝父类属性
        BeanUtils.copyProperties(sysRole, this);
        this.permissions = permissions;
        this.setPermissionIds(permissions.stream().map(SysPermission::getId).toList());
    }
}
