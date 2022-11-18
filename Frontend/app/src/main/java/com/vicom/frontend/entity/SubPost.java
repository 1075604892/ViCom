package com.vicom.frontend.entity;

public class SubPost {
    private String id;//帖子id
    private String username;//用户名
    private String iconUrl;//头像url
    private String rid;

    private String replyName; //被回复人id
    private String type;
    private String content;//内容
    private String picUrl;//图片路径

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRid() {
        return rid;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setType(String type) {
        this.type = type;
    }
}
