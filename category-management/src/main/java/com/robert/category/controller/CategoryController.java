package com.robert.category.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.robert.category.constant.CategoryConstant;
import com.robert.category.exception.ApplicationException;
import com.robert.category.exception.InvalidNodeException;
import com.robert.category.exception.NodeExistsException;
import com.robert.category.exception.RequiredFieldException;
import com.robert.category.service.CategoryService;
import com.robert.category.ui.model.ErrorResponseVO;
import com.robert.category.ui.model.NodeVO;

import lombok.extern.slf4j.Slf4j;

/***
 * 
 * @author robert
 *
 */
@Slf4j
@RestController
@RequestMapping("/nodes")
@CrossOrigin("*")
public class CategoryController {

	private final CategoryService service;

	/***
	 * Constructor injection
	 * 
	 * @param service
	 */
	@Autowired
	CategoryController(CategoryService service) {
		this.service = service;
	}

	/**
	 * This API is using for the test the rest service
	 * 
	 * @return
	 */
	@GetMapping
	public String testApi() {
		return "Hello API";
	}

	/***
	 * This API is used for get the all node name with node id to display in
	 * dropdown list for add/delete/edit node based on selected node URL :
	 * http://localhost:8081/api/v1/nodes/fetchNodes
	 * 
	 * @return
	 */
	@GetMapping("/getNodeNames")
	public Map<Long, String> getNodeNames() {
		log.debug("{} getNodeNames {}", CategoryConstant.ENTERING, CategoryConstant.METHOD);
		Map<Long, String> nodes = service.getNodeNames();
		log.debug("{} getNodeNames {}", CategoryConstant.EXITING, CategoryConstant.METHOD);
		return nodes;
	}
	
	/***
	 * This API is using for for get All the node hierarchy. It will return the all
	 * parent and child node from DB It will check the parentID is null which means
	 * root id so we can load the the UI hierarchy structure URL:
	 * http://localhost:8081/api/v1/nodes/getNodeNames
	 * 
	 * @return
	 */
	@GetMapping("/fetchNodes")
	public List<NodeVO> getNodes() {
		log.debug("{} getAllNodes {}", CategoryConstant.ENTERING, CategoryConstant.METHOD);
		List<NodeVO> nodes = service.getNodeHierarchy();
		log.debug("{} getAllNodes {}", CategoryConstant.EXITING, CategoryConstant.METHOD);
		return nodes;
	}

	/***
	 * createNode API is using for create the root node/child node. we can create
	 * only one root node. it won't all to create the multiple root node but we can
	 * create the N number of parent/child name To create root node we have to pass
	 * the name alone in the request To create parent/child node we have to pass the
	 * the node name and the parentid nodeId is auto increment id URL:
	 * http://localhost:8081/api/v1/nodes/createNode Root node create request
	 * {"name": "Fashion"} child/parent node create request: { "name": "T-Shirt",
	 * "parentId": 9} parentID will it get it from getNodeNames API
	 * 
	 * @param nodeVo
	 * @return
	 */
	@PostMapping("/createNode")
	@ResponseStatus(HttpStatus.CREATED)
	public NodeVO addNode(@RequestBody NodeVO nodeVo) {
		log.debug("{} addNode {}", CategoryConstant.ENTERING, CategoryConstant.METHOD);

		Optional<String> nodeName = Optional.ofNullable(nodeVo.getName());
		// if the request is doesn't have name then it will throw the error otherwise
		// create the node based on business logic
		if (!nodeName.isPresent()) {
			throw new RequiredFieldException("Node name shouldn't be null", "RE001", false);
		} else {
			NodeVO vo = service.createNode(nodeVo);
			log.debug("{} addNode {}", CategoryConstant.EXITING, CategoryConstant.METHOD);

			return vo;
		}
	}

	/***
	 * editNode API is using for the edit the node if present in db otherwise it
	 * will throw the error we couldn't able to edit the root node but we can edit
	 * the the parent/child node. URL: http://localhost:8081/api/v1/nodes/editNode
	 * Request: {"nodeId": 13, "name": "Robert", "parentId": 10}
	 * 
	 * @param nodeVo
	 * @return
	 */
	@PostMapping("/editNode")
	@ResponseStatus(HttpStatus.OK)
	public NodeVO editNode(@RequestBody NodeVO nodeVo) {
		log.debug("{} editNode {}", CategoryConstant.ENTERING, CategoryConstant.METHOD);

		Optional<String> nodeName = Optional.ofNullable(nodeVo.getName());
		if (!nodeName.isPresent()) {
			throw new RequiredFieldException("Node name shouldn't be null", "RE001", false);
		} else {
			NodeVO vo = service.editNode(nodeVo);
			log.debug("{} editNode {}", CategoryConstant.EXITING, CategoryConstant.METHOD);

			return vo;
		}
	}

	/***
	 * This API is using for delete the node we can delete parent/root/child node.
	 * if we delete the parent node and will delete child node if parent node has
	 * children, as well as can delete the child node URL:
	 * http://localhost:8081/api/v1/nodes/removeNode request: {"nodeId": 10, "name":
	 * "Robert"}
	 * 
	 * @param nodeVo
	 * @return
	 */
	@PostMapping("/removeNode")
	@ResponseStatus(HttpStatus.OK)
	public String deleteNode(@RequestBody NodeVO nodeVo) {
		log.debug("{} deleteNode {}", CategoryConstant.ENTERING, CategoryConstant.METHOD);

		Optional<String> nodeName = Optional.ofNullable(nodeVo.getName());
		if (!nodeName.isPresent()) {
			throw new RequiredFieldException("Node name shouldn't be null", "RE001", false);
		} else {
			String status = service.removeNode(nodeVo);
			log.debug("{} deleteNode {}", CategoryConstant.EXITING, CategoryConstant.METHOD);

			return status;
		}
	}

	@ExceptionHandler(RequiredFieldException.class)
	public final ResponseEntity<ErrorResponseVO> handleAllExceptions(RequiredFieldException e) {
		return new ResponseEntity<>(new ErrorResponseVO(e.getErrorMessage(), e.getErrorCode(), e.isStatus()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NodeExistsException.class)
	public final ResponseEntity<ErrorResponseVO> handleAllExceptons(NodeExistsException e) {
		return new ResponseEntity<>(new ErrorResponseVO("A node with this ID already exists", "NE001", false),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidNodeException.class)
	public final ResponseEntity<ErrorResponseVO> handleAllExceptions(InvalidNodeException e) {
		return new ResponseEntity<>(
				new ErrorResponseVO("The specified node " + e.getNodeId() + " does not exist", "NE001", false),
				HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(ApplicationException.class)
	public final ResponseEntity<ErrorResponseVO> handleAllExceptions(ApplicationException e) {
		return new ResponseEntity<>(new ErrorResponseVO(e.getErrorMessage(), e.getErrorCode(), e.isStatus()),
				HttpStatus.NOT_FOUND);
	}

}
