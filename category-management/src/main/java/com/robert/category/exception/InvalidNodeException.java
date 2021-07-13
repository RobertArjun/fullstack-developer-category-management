package com.robert.category.exception;

public class InvalidNodeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6114244572090853729L;
	
	private final long nodeId;

	public InvalidNodeException(long nodeId) {
		this.nodeId = nodeId;
	}

	public long getNodeId() {
		return nodeId;
	}

}
