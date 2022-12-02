package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.requestEntry.RequestPost;
import com.vicom.backend.requestEntry.RequestSubPost;
import com.vicom.backend.entryVO.PostVO;
import com.vicom.backend.entryVO.SubPostVO;
import com.vicom.backend.service.PostService;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    public R<ArrayList<PostVO>> postList(@RequestBody RequestPost requestPost) {
        return postService.findPostsByCid(requestPost.getCid(), PageRequest.of(requestPost.getPage(), 10));
    }

    @PostMapping("/querySubPostsByPid")
    @ResponseBody
    public R<ArrayList<SubPostVO>> subPostList(@RequestBody RequestSubPost requestSubPost) {
        return postService.findSubPostsByPid(requestSubPost.getPid(), PageRequest.of(requestSubPost.getPage(), 10));
    }
}
