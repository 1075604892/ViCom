package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import com.vicom.backend.requestEntry.RequestPost;
import com.vicom.backend.service.PostService;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
    public R<Page<Post>> postList(@RequestBody RequestPost requestPost) {
        return postService.findPostsByCid(requestPost.getCid(), PageRequest.of(requestPost.getPage(), 10));
    }
}
