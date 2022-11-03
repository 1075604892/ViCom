package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.User;
import com.vicom.backend.service.CommunityService;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @PostMapping("/favoriteCommunities")
    @ResponseBody
    public R<List<Community>> register(@RequestBody User user) {
        return communityService.followCommunities(user);
    }
}
