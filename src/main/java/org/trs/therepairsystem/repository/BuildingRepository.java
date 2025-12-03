package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.Building;

import java.util.List;
import java.util.Optional;

/**
 * 楼栋数据访问层
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    Optional<Building> findByName(String name);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    @Query("SELECT b FROM Building b WHERE b.name LIKE %:name%")
    Page<Building> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    List<Building> findAllByOrderByName();
    
    Page<Building> findAllByOrderByName(Pageable pageable);
}