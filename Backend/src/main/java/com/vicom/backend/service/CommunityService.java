package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.FollowCommunity;
import com.vicom.backend.entity.User;
import com.vicom.backend.repository.CommunityRepository;
import com.vicom.backend.repository.FollowCommunityRepository;
import com.vicom.backend.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private FollowCommunityRepository followCommunityRepository;

    public R<List<Community>> followCommunities(User user) {
        List<FollowCommunity> allFollowCommunities = followCommunityRepository.findByUid(user.getId());

        List<Community> communities = new ArrayList<Community>();

        for (FollowCommunity item : allFollowCommunities) {
            Community community = communityRepository.findById(item.getCid());
            community.setIsFollowed(followCommunityRepository.findByUid(user.getId()) != null ? Community.FOLLOWED_YES : Community.FOLLOWED_NO);
            community.setFollowNum(followCommunityRepository.countByCid(item.getCid()));

            communities.add(community);
        }

        return R.success(communities);
    }

    public R<List<Community>> search(String name, Long uid) {
        List<Community> communities = communityRepository.findByNameOrDescriptionContaining(name);

        for (Community community : communities) {
            community.setFollowNum(followCommunityRepository.countByCid(community.getId()));
            if (uid != null && uid != -1) {
                community.setIsFollowed(followCommunityRepository.findByUid(uid) != null ? Community.FOLLOWED_YES : Community.FOLLOWED_NO);

            } else {
                community.setIsFollowed(Community.FOLLOWED_UNKNOWN);
            }
        }

        return R.success(communities);
    }
}
