package com.vicom.frontend.entity;

public class Post {
    private String id;//帖子id
    private String username;//用户名
    private String title;//标题
    private String content;//内容
    private String picUrl;//图片数

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
}
