package com.example.demo.dto;

public class TrendingItemDto {

    private String title;
    private String subtitle;
    private Long messagesCount;
    private Long activeCount;
    private String lastActivityLabel;
    private String url;

    public TrendingItemDto() {}

    public TrendingItemDto(String title, String subtitle, Long messagesCount,
                           Long activeCount, String lastActivityLabel, String url) {
        this.title = title;
        this.subtitle = subtitle;
        this.messagesCount = messagesCount;
        this.activeCount = activeCount;
        this.lastActivityLabel = lastActivityLabel;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public Long getMessagesCount() { return messagesCount; }
    public Long getActiveCount() { return activeCount; }
    public String getLastActivityLabel() { return lastActivityLabel; }
    public String getUrl() { return url; }

    public void setTitle(String title) { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setMessagesCount(Long messagesCount) { this.messagesCount = messagesCount; }
    public void setActiveCount(Long activeCount) { this.activeCount = activeCount; }
    public void setLastActivityLabel(String lastActivityLabel) { this.lastActivityLabel = lastActivityLabel; }
    public void setUrl(String url) { this.url = url; }
}