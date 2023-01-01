package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
    Community findById(Long id);

    @Query("SELECT c FROM Community c WHERE c.name LIKE %:name% OR c.description LIKE %:name%")
    List<Community> findByNameOrDescriptionContaining(@Param("name") String name);
}
