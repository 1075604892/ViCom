package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.FollowCommunity;
import com.vicom.backend.entity.User;
import com.vicom.backend.repository.CommunityRepository;
import com.vicom.backend.repository.FollowCommunityRepository;
import com.vicom.backend.repository.UserRepository;
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
            communities.addAll(communityRepository.findById(item.getCid()));
        }

        return R.success(communities);
    }

    public R<List<Community>> search(String name){
        return R.success(communityRepository.findByNameOrDescriptionContaining(name));
    }
}
