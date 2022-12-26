package com.vicom.frontend.entity;

public class User {
    private String id;
    private String username;
    private String nickname;
    // 性别 0：女性 1：男性 2：不公布
    private String sex;
    private String icon;
    private String email;
    private String level;
    private String privacy;
    private String introduce;

    // 状态 0：正常 1：限制
    private String status;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public String getSex() {
        return sex;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getIcon() {
        return icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
