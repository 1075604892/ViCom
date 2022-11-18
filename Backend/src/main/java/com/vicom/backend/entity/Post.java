package com.vicom.backend.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    private Long rid;//被回复帖子id

    private Long uid;//发帖者
    private String title;//标题
    private String content;//内容
    private Integer type;//帖子类型
    private String picUrl;//图片地址

    public static final Integer TYPE_POST = 0;
    public static final Integer TYPE_SUBPOST = 1;
    public static final Integer TYPE_REPLY = 2;
}
