package com.robert.category.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "node")
@Getter
@Setter
public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7100778071507237785L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String name;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Node parentNode;

	@OneToMany(mappedBy = "parentNode", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
	private List<Node> children;

	public void removeChild(Node node) {
		children.remove(node);
		node.setParentNode(null);
	}
	
	public void addChild(Node node) {
		children.remove(node);
		node.setParentNode(this);
	}
}
