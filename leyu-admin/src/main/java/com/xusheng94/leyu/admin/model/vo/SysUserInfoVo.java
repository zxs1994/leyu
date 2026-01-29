package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.common.util.CurrentUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserInfoVo extends SysUser {

    @Schema(description = "权限 code list")
    private List<String> permissionCodes;

    @Schema(description = "是否是平台用户")
    private boolean platformUser;

    public SysUserInfoVo(SysUser sysUser, List<String> permissionCodes) {
        // 拷贝父类属性
        BeanUtils.copyProperties(sysUser, this);
        this.permissionCodes = permissionCodes;
        this.platformUser = CurrentUser.isPlatformUser();
    }
}
