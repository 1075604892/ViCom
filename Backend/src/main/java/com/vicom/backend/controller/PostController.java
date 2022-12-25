package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.entryDTO.PostDTO;
import com.vicom.backend.entryDTO.SubPostDTO;
import com.vicom.backend.entryDTO.UserDTO;
import com.vicom.backend.entryVO.PostVO;
import com.vicom.backend.entryVO.SubPostVO;
import com.vicom.backend.service.PostService;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/post")
public class PostController {
    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @PostMapping("/queryPostsByCid")
    @ResponseBody
    public R<ArrayList<PostVO>> postList(@RequestBody PostDTO postDTO) {
        return postService.findPostsByCid(postDTO.getCid(), PageRequest.of(postDTO.getPage(), 10));
    }

    @PostMapping("/querySubPostsByPid")
    @ResponseBody
    public R<ArrayList<SubPostVO>> subPostList(@RequestBody SubPostDTO subPostDTO) {
        return postService.findSubPostsByPid(subPostDTO.getPid(), PageRequest.of(subPostDTO.getPage(), 10));
    }

    @PostMapping("/queryPostsByUid")
    @ResponseBody
    public R<ArrayList<PostVO>> queryPostsByUid(@RequestBody UserDTO userDTO) {
        return postService.findPostsByUid(userDTO.getUid(), PageRequest.of(0, 10));
    }

    @PostMapping("/search")
    @ResponseBody
    public R<List<PostVO>> search(@RequestBody NameDTO nameDTO) {
        return postService.search(nameDTO.getName());
    }
}
