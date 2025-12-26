package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LifecycleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private LocalDateTime eventTime;

    private String description;

    @ManyToOne
    private Asset asset;

    @PrePersist
    public void onCreate() {
        eventTime = LocalDateTime.now();
    }

    public LifecycleEvent() {}

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public String getDescription() {
        return description;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
