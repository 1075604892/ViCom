package com.vicom.backend.entryVO;

import com.vicom.backend.entity.Post;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PostVO {
    private Long id;//帖子id
    private String username;//用户名
    private String iconUrl;//头像url
    private Long uid;//用户uid

    private String title;//标题
    private String content;//内容
    private String picUrl;//图片路径

    private String releaseTime; //发表时间

    public PostVO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.picUrl = post.getPicUrl();
        this.releaseTime = post.getReleaseDate().toString().substring(0,16);
    }
}
