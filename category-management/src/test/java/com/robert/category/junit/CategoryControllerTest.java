package com.robert.category.junit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.robert.category.CategoryManagementApplication;
import com.robert.category.common.MockFactory;
import com.robert.category.ui.model.NodeVO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(classes = CategoryManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
@RunWith(SpringRunner.class)
class CategoryControllerTest {

	@LocalServerPort
	private int port;

	private URL base;

	@Autowired
	private TestRestTemplate template;

	RequestSpecification reqSpec;

	Response response;

	Map<Long, String> map;

	@Test
	@Order(1)
	void createRootNode() throws Exception {
		NodeVO createNodeRequest = MockFactory.createNodeFactory();
		this.base = new URL("http://localhost:" + port + "/api/v1/nodes/createNode");
		ResponseEntity<NodeVO> response = template.postForEntity(base.toString(), createNodeRequest, NodeVO.class);
		assertThat(response.getStatusCode().compareTo(HttpStatus.CREATED));
		assertNotNull(response.getBody());
	}

	@Test
	@Order(2)
	void createMultipleRootNode() throws Exception {
		NodeVO createNodeRequest = MockFactory.createNodeFactory();
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/createNode");
		Assert.assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
	}

	@Test
	@Order(3)
	void getRootNodeNames() throws Exception {
		reqSpec = new RequestSpecBuilder().setBaseUri("http://localhost:" + port).setContentType(ContentType.JSON)
				.build();
		response = given().spec(reqSpec).when().get("/api/v1/nodes/getNodeNames");
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		String a = response.getBody().asString();
		System.out.println(a);
	}

	@Test
	@Order(4)
	void createFirstParentCategory() throws Exception {
		NodeVO createNodeRequest = MockFactory.createParentNodeFactory("Men", 1);
		this.base = new URL("http://localhost:" + port + "/api/v1/nodes/createNode");
		ResponseEntity<NodeVO> response = template.postForEntity(base.toString(), createNodeRequest, NodeVO.class);
		assertThat(response.getStatusCode().compareTo(HttpStatus.CREATED));
		assertNotNull(response.getBody());
	}

	@Test
	@Order(5)
	void createSecondParentCategory() throws Exception {
		NodeVO createNodeRequest = MockFactory.createParentNodeFactory("Women", 1);
		this.base = new URL("http://localhost:" + port + "/api/v1/nodes/createNode");
		ResponseEntity<NodeVO> response = template.postForEntity(base.toString(), createNodeRequest, NodeVO.class);
		assertThat(response.getStatusCode().compareTo(HttpStatus.CREATED));
		assertNotNull(response.getBody());
	}

	public static Optional<Long> getKeysByValue(Map<Long, String> map, String value) {
		return map.entrySet().stream().filter(entry -> entry.getValue().equals(value)).map(Map.Entry::getKey)
				.findFirst();
	}

	@Test
	@Order(6)
	void editNode() throws Exception {
		NodeVO createNodeRequest = MockFactory.editNodeFactory(3, "Test", 2);
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/editNode");
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	}

	@Test
	@Order(7)
	void deleteNode() throws Exception {
		NodeVO createNodeRequest = MockFactory.deleteNodeFactory(3, "Test");
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/removeNode");
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	}

	@Test
	@Order(8)
	void editNode_EmptyRequest() throws Exception {
		NodeVO createNodeRequest = new NodeVO();
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/editNode");
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test
	@Order(9)
	void deleteNode_EmptyRequest() throws Exception {
		NodeVO createNodeRequest = new NodeVO();
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/removeNode");
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test
	@Order(10)
	void createNode_EmptyRequest() throws Exception {
		NodeVO createNodeRequest = new NodeVO();
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/createNode");
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test
	@Order(11)
	void editNodeInValidNodeId() throws Exception {
		NodeVO createNodeRequest = MockFactory.editNodeFactory(200, "Test", 100);
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/editNode");
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}

	@Test
	@Order(12)
	void deleteNodeInValidNodeId() throws Exception {
		NodeVO createNodeRequest = MockFactory.deleteNodeFactory(200, "Test");
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/removeNode");
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}

	@Test
	@Order(14)
	void deleteRootNode() throws Exception {
		NodeVO createNodeRequest = MockFactory.deleteNodeFactory(1, "Fashion");
		reqSpec = new RequestSpecBuilder().setBody(createNodeRequest).setBaseUri("http://localhost:" + port)
				.setContentType(ContentType.JSON).build();
		response = given().spec(reqSpec).when().post("/api/v1/nodes/removeNode");
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	}

	@Test
	@Order(15)
	void nodeNameFailure() throws Exception {
		reqSpec = new RequestSpecBuilder().setBaseUri("http://localhost:" + port).setContentType(ContentType.JSON)
				.build();
		response = given().spec(reqSpec).when().get("/api/v1/nodes/getNodeNames");
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}

	@Test
	@Order(16)
	void fetchNodeFailure() throws Exception {
		reqSpec = new RequestSpecBuilder().setBaseUri("http://localhost:" + port).setContentType(ContentType.JSON)
				.build();
		response = given().spec(reqSpec).when().get("/api/v1/nodes/fetchNodes");
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
	}
}
