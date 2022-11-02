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
    private Long id;
    private Long uid;
    private Long rid;
    private String title;
    private String content;
    private Integer type;
    private Integer picNum;

    public static final Integer TYPE_POST = 0;
    public static final Integer TYPE_SUBPOST = 1;
    public static final Integer TYPE_REPLY = 2;
}
