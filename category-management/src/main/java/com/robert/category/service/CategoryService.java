package com.robert.category.service;

import java.util.List;
import java.util.Map;

import com.robert.category.ui.model.NodeVO;

public interface CategoryService {

	List<NodeVO> getNodeHierarchy();

	NodeVO createNode(NodeVO nodeVo);

	NodeVO editNode(NodeVO nodeVo);

	String removeNode(NodeVO nodeVo);

	Map<Long, String> getNodeNames();

}
