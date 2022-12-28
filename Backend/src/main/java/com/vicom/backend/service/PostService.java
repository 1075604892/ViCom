package com.vicom.backend.service;

import com.vicom.backend.common.R;
import com.vicom.backend.entity.Post;
import com.vicom.backend.entity.User;
import com.vicom.backend.repository.ImageFileRepository;
import com.vicom.backend.repository.PostRepository;
import com.vicom.backend.repository.UserRepository;
import com.vicom.backend.entryVO.PostVO;
import com.vicom.backend.entryVO.SubPostVO;
import javafx.geometry.Pos;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageFileRepository imageFileRepository;

    public R<ArrayList<PostVO>> findPostsByCid(Long cid, Pageable pageable) {
        Page<Post> posts = postRepository.findByCidAndType(cid, Post.TYPE_POST, pageable);

        ArrayList<PostVO> postVOs = new ArrayList<>();
        for (Post post : posts) {
            PostVO postVO = new PostVO(post);

            User user = userRepository.findById(post.getUid());
            postVO.setUsername(user.getUsername());
            postVO.setIconUrl(user.getIcon());
            postVO.setUid(user.getId());
            postVOs.add(postVO);
        }

        return R.success(postVOs);
    }

    public R<ArrayList<PostVO>> findPostsByUid(Long uid, Pageable pageable) {
        Page<Post> posts = postRepository.findByUidAndType(uid, Post.TYPE_POST, pageable);

        ArrayList<PostVO> postVOs = new ArrayList<>();
        for (Post post : posts) {
            PostVO postVO = new PostVO(post);

            User user = userRepository.findById(post.getUid());
            postVO.setUsername(user.getUsername());
            postVO.setIconUrl(user.getIcon());
            postVO.setUid(user.getId());
            postVOs.add(postVO);
        }

        return R.success(postVOs);
    }

    public R<ArrayList<SubPostVO>> findSubPostsByPid(Long pid, Pageable pageable) {
        Page<Post> subPosts = postRepository.findByPidAndType(pid, Post.TYPE_SUBPOST, pageable);
        ArrayList<Post> allReplies = postRepository.findByPidAndType(pid, Post.TYPE_REPLY);

        ArrayList<SubPostVO> subPostVOs = new ArrayList<>();
        for (Post subPost : subPosts) {
            SubPostVO subPostVO = new SubPostVO(subPost);

            User user = userRepository.findById(subPost.getUid());
            subPostVO.setUsername(user.getUsername());
            subPostVO.setIconUrl(user.getIcon());
            subPostVO.setUid(user.getId());
            subPostVOs.add(subPostVO);

            for (Post reply : allReplies) {
                if (Objects.equals(subPost.getId(), reply.getRid())) {
                    User user1 = userRepository.findById(reply.getUid());

                    SubPostVO replyVO = new SubPostVO(reply);
                    replyVO.setUsername(user1.getUsername());
                    replyVO.setIconUrl(user1.getIcon());
                    replyVO.setUid(user1.getId());

                    User user2 = userRepository.findById(reply.getRUid());
                    replyVO.setReplyName(user2.getUsername());

                    subPostVOs.add(replyVO);
                }
            }
        }
        return R.success(subPostVOs);
    }

    public R<PostVO> getPostInformationByPid(Long pid) {
        Post post = postRepository.findById(pid);
        PostVO postVO = new PostVO(post);

        User user = userRepository.findById(post.getUid());

        postVO.setUsername(user.getUsername());
        postVO.setIconUrl(user.getIcon());

        return R.success(postVO);
    }

    public R<List<PostVO>> search(String name) {
        List<Post> posts = postRepository.findByTitleOrContentContaining(name);
        List<PostVO> postVOS = new ArrayList<>();

        for (Post post : posts) {
            PostVO postVO = new PostVO(post);
            postVO.setUsername(userRepository.findById(post.getUid()).getUsername());

            postVOS.add(postVO);
        }

        return R.success(postVOS);
    }

    public R<String> releasePost(MultipartFile[] images) {
        try {
            for (MultipartFile image : images) {
                String imageName = image.getOriginalFilename();
                if (imageName == null) {
                    return R.error("没有图片");
                } else {
                    //保存图片
                    String extension = imageName.substring(imageName.lastIndexOf("."));
                    UUID uuid = UUID.randomUUID();
                    String path = imageFileRepository.saveImage(image.getBytes(), uuid + extension);
                    return R.success("发帖成功");
                }
            }
        } catch (Exception e) {
            return R.error("遇到错误");
        }
        return R.error("遇到未知错误");
    }

    public R<List<SubPostVO>> getReplyByUid(Long uid, String cookie, Integer page) {
        User user = userRepository.findById(uid);
        String realCookie = DigestUtils.md5Hex(user.getUsername() + user.getPassword() + "security");

        if (!realCookie.equals(cookie)) {
            return R.error("用户没有权限查看回复列表");
        }

        Page<Post> posts = postRepository.findByRUidAndTypeOrderByReleaseDateDesc(uid, PageRequest.of(page, 10));
        List<SubPostVO> postVOS = new ArrayList<>();

        for (Post post : posts) {
            SubPostVO postVO = new SubPostVO(post);
            postVO.setUsername(userRepository.findById(post.getUid()).getUsername());
            postVOS.add(postVO);
        }


        return R.success(postVOS);
    }
}
