package com.github.zxs1994.java_template.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionTreeNode {

    private Long id;
    private String code;
    private String name;
    private String path;
    private String method;
    private Integer authLevel;
    private Long parentId;

    private List<PermissionTreeNode> children = new ArrayList<>();
}
