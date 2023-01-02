package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.entryVO.UserVO;
import com.vicom.backend.repository.ImageFileRepository;
import com.vicom.backend.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageFileRepository imageFileRepository;

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

        if (user.getIntroduce() == null) {
            user.setIntroduce("这位用户很懒，还没有简介哦");
        }

        if (user.getPrivacy() == null) {
            user.setPrivacy(User.PRIVACY_PUBLIC);
        }

        user.setLevel(0);
        user.setStatus(User.STATUS_NORMAL);
        userRepository.save(user);
        return R.success("注册成功");
    }

    public R<Object> login(User user) {
        System.out.println(user.toString());
        if (userRepository.existsByUsernameAndPassword(user.getUsername(), user.getPassword())) {
            User userInfo = userRepository.findByUsername(user.getUsername());

            String cookie = DigestUtils.md5Hex(userInfo.getUsername() + userInfo.getPassword() + "security");
            Map<String, Object> map = new HashMap<>();
            map.put("cookie", cookie);
            map.put("uid", userInfo.getId());

            return R.success("登录成功", map);
        }

        return R.error("登录失败，用户名或密码错误");
    }

    public R<Object> info(Long uid, String cookie) {
        User user = userRepository.findById(uid);

        if (user == null) {
            return R.error("用户uid不存在");
        }

        String realCookie = DigestUtils.md5Hex(user.getUsername() + user.getPassword() + "security");

        if (realCookie.equals(cookie)) {
            return R.success(new UserVO(user));
        } else if (Objects.equals(user.getPrivacy(), User.PRIVACY_PUBLIC)) {
            return R.success(new UserVO(user));
        }

        return R.error("Cookie错误");
    }

    public R<List<UserVO>> search(NameDTO nameDTO) {
        List<User> users = userRepository.findByUsernameOrNicknameContaining(nameDTO.getName());
        List<UserVO> userVOS = new ArrayList<>();

        for (User user : users) {
            userVOS.add(new UserVO(user));
        }

        return R.success(userVOS);
    }

    public R<String> changeIcon(MultipartFile icon, String usernaem) {
        //User user = userRepository.findById(uid);
        User user = userRepository.findByUsername(usernaem);

        /*String realCookie = DigestUtils.md5Hex(user.getUsername() + user.getPassword() + "security");
        if (!realCookie.equals(cookie)) {
            return R.error("没有权限修改头像");
        }*/

        String imageName = icon.getOriginalFilename();
        String path = null;
        try {
            if (imageName != null) {
                //保存图片
                String extension = imageName.substring(imageName.lastIndexOf("."));
                UUID uuid = UUID.randomUUID();
                path = imageFileRepository.saveImage(icon.getBytes(), uuid + extension);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //保存信息
        user.setIcon(path);
        userRepository.save(user);
        return R.success("修改头像成功");
    }

    public R<String> change(User user) {
        User userOld = userRepository.findById(user.getId());

        if (user.getEmail() != null) {
            userOld.setEmail(user.getEmail());
        }

        if (user.getNickname() != null) {
            userOld.setNickname(user.getNickname());
        }

        if (user.getSex() != null) {
            userOld.setSex(user.getSex());
        }

        if (user.getIntroduce() != null) {
            userOld.setIntroduce(user.getIntroduce());
        }

        userRepository.save(userOld);
        return R.success("修改成功");
    }
}
