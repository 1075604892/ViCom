package com.vicom.backend.entryVO;


import com.vicom.backend.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    // 性别 0：女性 1：男性 2：不公布
    private Integer sex;
    private String icon;
    private String email;
    private Integer level;
    // 状态 0：正常 1：限制
    private Integer status;

    public UserVO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.sex = user.getSex();
        this.icon = user.getIcon();
        this.email = user.getEmail();
        this.level = user.getLevel();
        this.status = user.getStatus();
    }
}
