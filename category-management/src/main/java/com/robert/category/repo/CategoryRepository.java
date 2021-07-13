package com.robert.category.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.robert.category.entity.Node;
import com.robert.category.ui.model.NodeVO;

@Repository
public interface CategoryRepository extends JpaRepository<Node, Long> {
	Node findByName(String name);

	@Query(value = "select n from Node n inner join n.parentNode p where p.id = ?", nativeQuery = true)
	List<Node> findNodesByParentId(Long parentId);
	
	Node findByParentNodeNull();

}
