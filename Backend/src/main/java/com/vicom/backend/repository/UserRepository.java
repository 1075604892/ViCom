package com.vicom.backend.repository;

import com.vicom.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findById(Long id);

    //User findByUsernameAndPassword(String username, String password);

    Boolean existsByUsernameAndPassword(String username, String password);

    //Page<User> findByUsernameLikeOrNicknameLike(String username, String nickname, Pageable pageable);

    List<User> findByUsernameLike(String username);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:name% OR u.nickname LIKE %:name%")
    List<User> findByUsernameOrNicknameContaining(@Param("name") String name);
}
