package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public R<Page<Post>> findPostsByCid(Long cid, Pageable pageable) {
        Page<Post> posts = postRepository.findByCid(cid, pageable);
        return R.success(posts);
    }
}
