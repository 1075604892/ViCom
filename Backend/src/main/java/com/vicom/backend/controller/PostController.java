package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.User;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/post")
public class PostController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    @ResponseBody
    public R<String> postList(@RequestBody Long cid) {
        return R.success("成功返回");
    }
}
