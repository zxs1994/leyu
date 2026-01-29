package com.xusheng94.leyu.admin.model.dto;

import com.xusheng94.leyu.admin.entity.SysDept;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TenantDto extends SysDept {
     private SysUserDto adminUser;
}
