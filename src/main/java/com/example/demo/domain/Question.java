package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 4000)
    private String body;

    private String askedBy;



    private String status = "OPEN";

    private Integer maxClaimers = 3;

    private Long acceptedAnswerId;

    private Instant createdAt = Instant.now();

    private String imageUrl;


@Column(length = 100)
private String groupName;  

public String getGroupName() { return groupName; }
public void setGroupName(String groupName) { this.groupName = groupName; }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getAskedBy() { return askedBy; }
    public void setAskedBy(String askedBy) { this.askedBy = askedBy; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMaxClaimers() { return maxClaimers; }
    public void setMaxClaimers(Integer maxClaimers) { this.maxClaimers = maxClaimers; }

    public Long getAcceptedAnswerId() { return acceptedAnswerId; }
    public void setAcceptedAnswerId(Long acceptedAnswerId) { this.acceptedAnswerId = acceptedAnswerId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}