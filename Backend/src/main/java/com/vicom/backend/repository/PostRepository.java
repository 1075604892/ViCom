package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(Long id);

    Page<Post> findByCidAndType(Long cid, Integer type, Pageable pageable);

    Page<Post> findByPidAndType(Long pid, Integer type, Pageable pageable);

    ArrayList<Post> findByPidAndType(Long rid, Integer type);
}
