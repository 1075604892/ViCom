package com.vicom.backend.repository;

import com.vicom.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findById(Long id);

    //User findByUsernameAndPassword(String username, String password);

    Boolean existsByUsernameAndPassword(String username, String password);

    Page<User> findByUsernameLikeOrNicknameLike(String username, String nickname, Pageable pageable);
}
