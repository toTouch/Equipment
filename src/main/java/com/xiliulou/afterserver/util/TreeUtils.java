package com.xiliulou.afterserver.util;



import com.xiliulou.afterserver.entity.PermissionResource;
import com.xiliulou.afterserver.entity.PermissionResourceTree;
import com.xiliulou.afterserver.entity.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: eclair
 * @Date: 2020/12/14 11:06
 * @Description:
 */
public class TreeUtils {
	public static List<PermissionResourceTree> buildTree(List<PermissionResource> permissionResources, Long menuRoot) {
		List<PermissionResourceTree> trees = new ArrayList<>();

		PermissionResourceTree node;
		for (PermissionResource permissionResource : permissionResources) {
			node = new PermissionResourceTree();
			node.setId(permissionResource.getId());
			node.setCreateTime(permissionResource.getCreateTime());
			node.setDesc(permissionResource.getDesc());
			node.setMethod(permissionResource.getMethod());
			node.setName(permissionResource.getName());
			node.setType(permissionResource.getType());
			node.setUri(permissionResource.getUri());
			node.setParentId(permissionResource.getParent());
			node.setSort(permissionResource.getSort());
			trees.add(node);
		}

		return build(trees,menuRoot);
	}

	private static  <T extends TreeNode> List<T> build(List<T> treeNodes, Long root) {
		List<T> trees = new ArrayList<>();

		for (T treeNode : treeNodes) {

			if (root.equals(treeNode.getParentId())) {
				trees.add(treeNode);
			}

			for (T it : treeNodes) {
				if (it.getParentId() == treeNode.getId()) {
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<>());
					}
					treeNode.add(it);
				}
			}
		}
		return trees;
	}
}
