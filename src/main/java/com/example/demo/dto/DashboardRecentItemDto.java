package com.example.demo.dto;

public class DashboardRecentItemDto {


    private String type;

    private Long id;
    private String title;
    private String subtitle;
    private String meta; 

    public DashboardRecentItemDto() {
    }

    public DashboardRecentItemDto(String type, Long id, String title, String subtitle, String meta) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.meta = meta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}