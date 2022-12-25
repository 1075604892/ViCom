package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.User;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.entryDTO.UserDTO;
import com.vicom.backend.entryVO.UserVO;
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
        return userService.register(user);
    }

    @PostMapping("/login")
    @ResponseBody
    public R<Object> login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping("/info")
    @ResponseBody
    public R<Object> info(@RequestBody UserDTO userDTO) {
        return userService.info(userDTO.getUid(), userDTO.getCookie());
    }

    @PostMapping("/search")
    @ResponseBody
    public R<List<UserVO>> search(@RequestBody NameDTO nameDTO) {
        return userService.search(nameDTO);
    }
}
