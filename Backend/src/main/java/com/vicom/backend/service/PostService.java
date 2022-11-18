package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import com.vicom.backend.repository.PostRepository;
import com.vicom.backend.repository.UserRepository;
import com.vicom.backend.responseEntry.ResponsePost;
import com.vicom.backend.responseEntry.ResponseSubPost;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public R<ArrayList<ResponsePost>> findPostsByCid(Long cid, Pageable pageable) {
        Page<Post> posts = postRepository.findByCidAndType(cid, Post.TYPE_POST, pageable);

        ArrayList<ResponsePost> responsePosts = new ArrayList<>();
        for (Post post : posts) {
            ResponsePost responsePost = new ResponsePost(post);

            User user = userRepository.findById(post.getUid());
            responsePost.setUsername(user.getUsername());
            responsePost.setIconUrl(user.getIcon());
            responsePosts.add(responsePost);
        }

        return R.success(responsePosts);
    }

    public R<ArrayList<ResponseSubPost>> findSubPostsByPid(Long pid, Pageable pageable) {
        Page<Post> posts = postRepository.findByPid(pid, pageable);

        ArrayList<ResponseSubPost> responseSubPosts = new ArrayList<>();
        for (Post post : posts) {
            ResponseSubPost responseSubPost = new ResponseSubPost(post);

            User user = userRepository.findById(post.getUid());
            responseSubPost.setUsername(user.getUsername());
            responseSubPost.setIconUrl(user.getIcon());

            //如果是回复，获取回复对应的人的id
            if (Objects.equals(post.getType(), Post.TYPE_REPLY)) {
                Post repliedPost = postRepository.findById(post.getRid());
                User user1 = userRepository.findById(repliedPost.getUid());

                responseSubPost.setReplyName(user1.getUsername());
            }

            responseSubPosts.add(responseSubPost);
        }

        return R.success(responseSubPosts);
    }
}
