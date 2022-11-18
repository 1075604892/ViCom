package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.repository.PostRepository;
import com.vicom.backend.repository.UserRepository;
import com.vicom.backend.responseEntry.ResponsePost;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public R<ArrayList<ResponsePost>> findPostsByCid(Long cid, Pageable pageable) {
        Page<Post> posts = postRepository.findByCid(cid, pageable);

        ArrayList<ResponsePost> responsePosts = new ArrayList<>();
        for (Post post : posts) {
            ResponsePost responsePost = new ResponsePost(post);
            responsePost.setUsername(userRepository.findById(post.getUid()).getUsername());
            responsePosts.add(responsePost);
        }

        return R.success(responsePosts);
    }
}
