package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.common.util.TreeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPermissionTreeNode extends SysPermission implements TreeUtils.TreeNode, TreeUtils.HasChildren<SysPermissionTreeNode> {

    @Schema(description = "🌿树枝", example = "[]")
    private List<SysPermissionTreeNode> children = new ArrayList<>();

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public Long getParentId() {
        return super.getParentId();
    }
}
