package com.vicom.backend.controller;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entryDTO.NameDTO;
import com.vicom.backend.entryDTO.PostDTO;
import com.vicom.backend.entryDTO.SubPostDTO;
import com.vicom.backend.entryDTO.UserDTO;
import com.vicom.backend.entryVO.PostVO;
import com.vicom.backend.entryVO.SubPostVO;
import com.vicom.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    //根据社区获取所有帖子
    @PostMapping("/queryPostsByCid")
    @ResponseBody
    public R<ArrayList<PostVO>> postList(@RequestBody PostDTO postDTO) {
        return postService.findPostsByCid(postDTO.getCid(), PageRequest.of(postDTO.getPage(), 10));
    }

    //根据帖子id获取帖子的信息
    @PostMapping("/getPostInformationByPid")
    @ResponseBody
    public R<PostVO> getPostInformationByPid(@RequestBody PostDTO postDTO) {
        return postService.getPostInformationByPid(postDTO.getPid());
    }

    //根据帖子id获取帖子的所有回复（不包括主楼）
    @PostMapping("/querySubPostsByPid")
    @ResponseBody
    public R<ArrayList<SubPostVO>> subPostList(@RequestBody SubPostDTO subPostDTO) {
        return postService.findSubPostsByPid(subPostDTO.getPid(), PageRequest.of(subPostDTO.getPage(), 10));
    }

    //根据单个用户的所有发帖
    @PostMapping("/queryPostsByUid")
    @ResponseBody
    public R<ArrayList<PostVO>> queryPostsByUid(@RequestBody UserDTO userDTO) {
        return postService.findPostsByUid(userDTO.getUid(), PageRequest.of(0, 10));
    }

    //查找帖子
    @PostMapping("/search")
    @ResponseBody
    public R<List<PostVO>> search(@RequestBody NameDTO nameDTO) {
        return postService.search(nameDTO.getName());
    }

    //发表帖子
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public R<String> releasePost(@RequestPart("images") MultipartFile[] images, @RequestPart("post") Post post, @RequestPart("cookie") String cookie) {
        return postService.releasePost(images, post, cookie);
    }

    @PostMapping("/getReplyByUid")
    @ResponseBody
    public R<List<SubPostVO>> getReplyByUid(@RequestBody UserDTO userDTO) {
        return postService.getReplyByUid(userDTO.getUid(), userDTO.getCookie(), userDTO.getPage());
    }
}
