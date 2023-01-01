package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(Long id);

    Page<Post> findByCidAndTypeOrderByReleaseDateDesc(Long cid, Integer type, Pageable pageable);

    Page<Post> findByUidAndType(Long uid, Integer type, Pageable pageable);

    Page<Post> findByPidAndType(Long pid, Integer type, Pageable pageable);

    ArrayList<Post> findByPidAndType(Long rid, Integer type);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% OR p.content LIKE %:title%")
    List<Post> findByTitleOrContentContaining(@Param("title") String title);

    @Query("select p from Post p where p.rUid= :ruid and (p.type= 1 or p.type=2)" +
            " order by p.releaseDate desc")
    Page<Post> findByRUidAndTypeOrderByReleaseDateDesc(Long ruid, Pageable pageable);
}
