package com.vicom.frontend.entity;

public class Community {
    private String name;
    private String cover_path;
    private String cid;
    private String description;

    public String getCid() {
        return cid;
    }

    public String getCover_path() {
        return cover_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCover_path(String cover_path) {
        this.cover_path = cover_path;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
