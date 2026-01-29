package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.model.dto.SysUserDto;
import com.xusheng94.leyu.admin.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserVo extends SysUserDto {

    @Schema(description = "角色 list")
    private List<SysRole> roles;

    @Schema(description = "部门名称")
    private String deptName;

}
