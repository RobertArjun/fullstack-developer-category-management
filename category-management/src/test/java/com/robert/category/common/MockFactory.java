package com.robert.category.common;

import com.robert.category.ui.model.NodeVO;

public class MockFactory {

	public MockFactory() {
	}

	public static NodeVO createNodeFactory() {
		NodeVO vo = new NodeVO();
		vo.setName("Fashion");
		return vo;
	}

	public static NodeVO createParentNodeFactory(String name, long n) {
		NodeVO vo = new NodeVO();
		vo.setName(name);
		vo.setParentId(n);
		return vo;
	}

	public static NodeVO editNodeFactory(int nodeID, String name, int parentID) {
		NodeVO vo = new NodeVO();
		vo.setName(name);
		vo.setParentId(parentID);
		vo.setNodeId(nodeID);
		return vo;
	}

	public static NodeVO deleteNodeFactory(int nodeId, String name) {
		NodeVO vo = new NodeVO();
		vo.setName(name);
		vo.setNodeId(nodeId);
		return vo;
	}

}
