package com.robert.category.ui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeVO {
	public long nodeId;
	private String name;
	public long parentId;
	public List<NodeVO> children;

	public NodeVO(long nodeId, String name, long parentId, List<NodeVO> children) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.parentId = parentId;
		this.children = children;
	}

	public NodeVO(long nodeId, String name, long parentId) {
		this(nodeId, name, parentId, null);
	}

}
