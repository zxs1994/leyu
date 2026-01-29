package com.xusheng94.leyu.common.util;

import java.util.*;
import java.util.stream.Collectors;

public class TreeUtils {
    public static final long ROOT_ID = 0L;
    /**
     * 构建通用树
     * @param items 原始节点列表
     * @param <T> 节点类型，需要实现 TreeNode 接口
     */
    public static <T extends TreeNode> List<T> buildTree(List<T> items) {

        Map<Long, List<T>> groupByParent = items.stream()
                .collect(Collectors.groupingBy(
                        item -> Optional.ofNullable(item.getParentId()).orElse(ROOT_ID)
                ));

        return buildTreeRecursive(groupByParent, ROOT_ID);
    }

    private static <T extends TreeNode> List<T> buildTreeRecursive(Map<Long, List<T>> map, Long parentId) {
        List<T> children = map.get(parentId);
        if (children == null) return Collections.emptyList();

        for (T child : children) {
            if (child instanceof HasChildren) {
                ((HasChildren<T>) child).setChildren(buildTreeRecursive(map, child.getId()));
            }
        }

        return children;
    }

    /**
     * 节点可挂载子节点
     */
    public interface HasChildren<T> {
        void setChildren(List<T> children);
    }

    /**
     * 树节点接口，要求提供id和parentId
     */
    public static interface TreeNode {
        Long getId();
        Long getParentId();
    }
}