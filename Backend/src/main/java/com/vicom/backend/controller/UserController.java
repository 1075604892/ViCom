package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.User;
import com.vicom.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public R<String> register(@RequestBody User user) {
        System.out.println(user.toString());
        return userService.register(user);
    }
}
