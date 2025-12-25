package com.example.demo;

public class Reaction {
    private String type;   
    private String id;     
    private String emoji;  
    private String user;   

    public Reaction() {}

    public Reaction(String id, String emoji, String user) {
        this.type = "reaction";
        this.id = id;
        this.emoji = emoji;
        this.user = user;
    }

    // getters & setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
}
