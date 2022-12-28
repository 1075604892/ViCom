package com.vicom.backend.entryVO;

import com.vicom.backend.entity.Post;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class SubPostVO {
    private Long id;//帖子id
    private String username;//用户名
    private String iconUrl;//头像url
    private Long uid;//用户uid

    private Long rid;

    private String replyName; //被回复人id
    private Integer type;
    private String content;//内容
    private String picUrl;//图片路径

    private Long pid;

    public SubPostVO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.picUrl = post.getPicUrl();
        this.type = post.getType();
        this.rid = post.getRid();
        this.pid = post.getPid();
    }
}
