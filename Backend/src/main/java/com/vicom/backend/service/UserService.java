package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.User;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.entryVO.UserVO;
import com.vicom.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public R<String> register(User user) {
        // 判断是否有必填字段为空
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null
                || "".equals(user.getUsername()) || "".equals(user.getEmail()) || "".equals(user.getPassword())
        ) {
            return R.error("注册失败，必填字段为空");
        }

        //判断是否已经存在该用户
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return R.error("注册失败，该用户名已被使用");
        }

        if (user.getNickname() == null) {
            user.setNickname(user.getUsername());
        }

        if (user.getSex() == null) {
            user.setSex(User.SEX_SECRET);
        }

        if (user.getIcon() == null) {
            //设置默认头像路径
            //todo
        }

        user.setLevel(0);
        user.setStatus(User.STATUS_NORMAL);
        userRepository.save(user);
        return R.success("注册成功");
    }

    public R<Object> login(User user) {
        System.out.println(user.toString());
        if (userRepository.existsByUsernameAndPassword(user.getUsername(), user.getPassword())) {
            return R.success("登录成功");
        }

        return R.error("登录失败，用户名或密码错误");
    }

    public R<List<UserVO>> search(NameDTO nameDTO) {
        List<User> users = userRepository.findByUsernameOrNicknameContaining(nameDTO.getName());
        List<UserVO> userVOS = new ArrayList<>();

        for (User user : users) {
            userVOS.add(new UserVO(user));
        }

        return R.success(userVOS);
    }
}
