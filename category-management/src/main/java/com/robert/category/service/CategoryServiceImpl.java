package com.robert.category.service;

/****
 * @author robert
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.robert.category.entity.Node;
import com.robert.category.exception.ApplicationException;
import com.robert.category.exception.InvalidNodeException;
import com.robert.category.exception.NodeExistsException;
import com.robert.category.exception.RequiredFieldException;
import com.robert.category.repo.CategoryRepository;
import com.robert.category.ui.model.NodeVO;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryRepository repository;

	@Override
	@Transactional(readOnly = true)
	public Map<Long, String> getNodeNames() {
		List<Node> nodes = repository.findAll(); // get the all node
		if (nodes.isEmpty()) {
			throw new ApplicationException("Empty node", "AE002", false); // throw the exception if node is empty
		} else {
			// return the map
			return nodes.stream().collect(Collectors.toMap(Node::getId, Node::getName));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NodeVO> getNodeHierarchy() {
		Optional<Node> entity = Optional.ofNullable(repository.findByParentNodeNull()); // return the entity if parentId
																						// is null
		if (entity.isPresent() && entity.get().getId() > 0) {
			return getHierarchicalList(travelNode(entity.get()));
		} else {
			throw new ApplicationException("Empty node", "AE002", false);
		}
	}

	public List<NodeVO> getHierarchicalList(final List<NodeVO> list) {
		final List<NodeVO> copyList = new ArrayList<>(list);

		copyList.forEach(element -> {
			list.stream().filter(parent -> parent.nodeId == element.parentId).findAny().ifPresent(parent -> {
				if (parent.children == null) {
					parent.children = new ArrayList<>();
				}
				parent.children.add(element);
			});
		});
		list.subList(1, list.size()).clear();
		return list;
	}

	private List<NodeVO> travelNode(Node entity) {
		List<NodeVO> nodes = new ArrayList<>();
		fetchNodesRecursively(entity, nodes); // call recursive
		return nodes;
	}

	private void fetchNodesRecursively(Node entity, List<NodeVO> nodes) {
		if (null == entity.getParentNode()) {
			NodeVO create = new NodeVO(entity.getId(), entity.getName(), 0);// add root Note
			nodes.add(create);
		}
		if (entity.getChildren().size() > 0) {
			for (Node n : entity.getChildren()) {
				NodeVO create = new NodeVO(n.getId(), n.getName(), n.getParentNode().getId());
				nodes.add(create);
				fetchNodesRecursively(n, nodes); // call recursive
			}
		}
	}

	@Override
	@Transactional
	public NodeVO createNode(NodeVO nodeVo) {
		if (nodeVo.getParentId() == 0) {
			Optional<Node> entity = Optional.ofNullable(repository.findByParentNodeNull());
			if (entity.isPresent()) {
				throw new NodeExistsException();// handle the the multiple root node creation
			} else {
				Node node = new Node();
				node.setName(nodeVo.getName());
				Node n = repository.save(node); // create the root node
				return new NodeVO(n.getId(), n.getName(), 0);
			}
		} else {
			Node node = convertVoToEntity(nodeVo);
			Node n = repository.save(node); // create the parent/child node
			return new NodeVO(n.getId(), n.getName(), n.getParentNode().getId());
		}
	}

	/**
	 * map the vo to entity class
	 * 
	 * @param nodeVo
	 * @return
	 */
	private Node convertVoToEntity(NodeVO nodeVo) {
		Node node = new Node();
		node.setName(nodeVo.getName());

		Node parentNode = new Node();
		parentNode.setId(nodeVo.getParentId());
		node.setParentNode(parentNode);

		return node;
	}

	@Override
	@Transactional
	public NodeVO editNode(NodeVO nodeVo) {
		// handle root node edit
		if (nodeVo.getParentId() == 0 || nodeVo.getNodeId() == 0) {
			throw new RequiredFieldException("Root note couldn't be edit", "RE003", false);
		} else {
			// edit the parent/child node
			Optional<Node> entity = Optional.ofNullable(repository.getById(nodeVo.getNodeId()));
			if (!entity.isPresent()) {
				throw new InvalidNodeException(nodeVo.getNodeId()); // if node is empty throw the error
			} else {
				entity.get().setName(nodeVo.getName());
				Node parentNode = new Node();
				parentNode.setId(nodeVo.getParentId());
				entity.get().setParentNode(parentNode);
				Node n = repository.save(entity.get()); // edit the node
				return new NodeVO(n.getId(), n.getName(), n.getParentNode().getId());
			}
		}
	}

	@Override
	@Transactional
	public String removeNode(NodeVO nodeVo) {
		Optional<Node> entity = repository.findById(nodeVo.getNodeId());
		if (!entity.isPresent()) {
			throw new InvalidNodeException(nodeVo.getNodeId());
		} else {
			try {
				Node node = entity.get();
				node.removeChild(node); // remove foreign key relation
				repository.delete(node); // delete the node
				return "Successfully node deleted";
			} catch (Exception e) {
				throw new ApplicationException("Exception Occured while deleting node", "AE001", false);
			}
		}
	}

}
