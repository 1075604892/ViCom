package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(Long id);

    Page<Post> findByCidAndType(Long cid, Integer type, Pageable pageable);

    Page<Post> findByPidAndTypeOrType(Long rid,Integer type1,Integer type2, Pageable pageable);
}
