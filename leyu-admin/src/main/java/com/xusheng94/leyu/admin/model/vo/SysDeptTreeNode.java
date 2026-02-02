package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.util.TreeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptTreeNode extends SysDeptVo implements TreeUtils.TreeNode, TreeUtils.HasChildren<SysDeptTreeNode> {

    @Schema(description = "🌿树枝", example = "[]")
    private List<SysDeptTreeNode> children = new ArrayList<>();

    @Schema(description = "👤部门用户列表", example = "[]")
    private List<SysUser> users = new ArrayList<>();

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public Long getParentId() {
        return super.getParentId();
    }
}
