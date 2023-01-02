package com.vicom.frontend.entity;

import java.util.Date;

public class Post {
    private String id;//帖子id
    private String username;//用户名
    private String title;//标题
    private String content;//内容
    private String picUrl;//图片数
    private String releaseTime;
    private String iconUrl;
    private String uid;

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
