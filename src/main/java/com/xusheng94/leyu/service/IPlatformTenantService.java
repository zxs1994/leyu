package com.xusheng94.leyu.service;

import com.xusheng94.leyu.model.dto.TenantDto;
import com.xusheng94.leyu.entity.SysDept;
import com.xusheng94.leyu.model.vo.LoginVo;

import java.util.List;

public interface IPlatformTenantService {
    /**
     * 新增租户和默认管理员
     *
     * @param dto 租户信息和管理员信息
     * @return 新增租户ID
     */
    Long addTenantWithAdmin(TenantDto dto);

    List<SysDept> list();

    LoginVo switchTo(Long tenantId);
}
