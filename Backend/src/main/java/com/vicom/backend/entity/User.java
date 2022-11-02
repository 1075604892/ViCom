package com.vicom.backend.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    // 性别 0：女性 1：男性 2：不公布
    private Integer sex;
    private String icon;
    private String email;
    private Integer level;
    // 状态 0：正常 1：限制
    private Integer status;

    //一些常量
    public static final Integer SEX_FEMALE = 0;
    public static final Integer SEX_MALE = 1;
    public static final Integer SEX_SECRET = 2;

    public static final Integer STATUS_NORMAL = 0;
    public static final Integer STATUS_LIMIT = 1;

//    @Override
//    public String toString(){
//        return "name: " + name + ", email: " + email;
//    }
}
