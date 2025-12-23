package com.example.demo.dto;

public class ProfileLikeDto {

    private long likes;
    private boolean likedByMe;

    public ProfileLikeDto() {
    }

    public ProfileLikeDto(long likes, boolean likedByMe) {
        this.likes = likes;
        this.likedByMe = likedByMe;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }
}