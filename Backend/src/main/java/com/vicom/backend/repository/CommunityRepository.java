package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
    List<Community> findById(Long id);
}
