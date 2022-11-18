package com.vicom.backend.responseEntry;

import com.vicom.backend.entity.Post;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponsePost {
    private Long id;//帖子id
    private String username;//用户名
    private String title;//标题
    private String content;//内容
    private String picUrl;//图片数

    public ResponsePost(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.picUrl = post.getPicUrl();
    }
}
