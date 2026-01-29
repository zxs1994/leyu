package com.xusheng94.leyu.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xusheng94.leyu.config.LongListToStringSerializer;
import com.xusheng94.leyu.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleDto extends SysRole {

    @Schema(description = "权限 Ids")
    @JsonSerialize(using = LongListToStringSerializer.class)
    List<Long> permissionIds;

    public SysRoleDto(SysRole sysRole, List<Long> permissionIds) {
        BeanUtils.copyProperties(sysRole, this);
        this.permissionIds = permissionIds;
    }

    public SysRoleDto() {
    }
}
