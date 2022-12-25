package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Community;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.service.CommunityService;
import com.vicom.backend.service.PostService;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public R<List<Community>> getFavoriteCommunities(@RequestBody User user) {
        return communityService.followCommunities(user);
    }

    @PostMapping("/search")
    @ResponseBody
    public R<List<Community>> search(@RequestBody NameDTO nameDTO) {
        return communityService.search(nameDTO.getName());
    }
}
