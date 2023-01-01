package com.vicom.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cover;
    private String description;

    @Transient
    private Integer isFollowed;

    public static final Integer FOLLOWED_NO = 0;
    public static final Integer FOLLOWED_YES = 1;
    public static final Integer FOLLOWED_UNKNOWN = 2;

    @Transient
    private Long followNum;
}
