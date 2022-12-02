package com.vicom.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//帖子id
    private Long cid;//所在论坛id
    private Long pid;//所在帖子id

    private Long rid;//被回复帖子id，当帖子类型为0时，为空，当回复类型为1时，为主楼id，当回复类型为2时，为楼的id
    private Long rUid; //被回复人id

    private Long uid;//发帖者
    private String title;//标题
    private String content;//内容
    private Integer type;//帖子类型
    private String picUrl;//图片地址

    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;

    public static final Integer TYPE_POST = 0;
    public static final Integer TYPE_SUBPOST = 1;
    public static final Integer TYPE_REPLY = 2;
}
