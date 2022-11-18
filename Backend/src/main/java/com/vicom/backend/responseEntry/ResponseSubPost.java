package com.vicom.backend.responseEntry;

import com.vicom.backend.entity.Post;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseSubPost {
    private Long id;//帖子id
    private String username;//用户名
    private String iconUrl;//头像url
    private Long rid;

    private String replyName; //被回复人id
    private Integer type;
    private String content;//内容
    private String picUrl;//图片路径

    public ResponseSubPost(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.picUrl = post.getPicUrl();
        this.type = post.getType();
        this.rid = post.getRid();
    }
}
