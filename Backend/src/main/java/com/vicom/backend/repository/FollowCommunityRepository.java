package com.vicom.backend.repository;

import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.FollowCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowCommunityRepository extends JpaRepository<FollowCommunity, Integer> {
    List<FollowCommunity> findByUid(Long uid);

    FollowCommunity findByUidAndCid(Long uid, Long cid);

    Long countByCid(Long cid);
}
