package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String assetTag;

    private String assetType;

    private String model;

    private LocalDate purchaseDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User currentHolder;

    private LocalDateTime createdAt;

    public Asset() {

    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "AVAILABLE";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
     return id;
    }

    public void setId(Long id) {
     this.id = id;
    }

    public String getAssetTag() {
     return assetTag;
    }

    public void setAssetTag(String assetTag) {
     this.assetTag = assetTag;
    }

    public String getAssetType() {
     return assetType;
    }

    public void setAssetType(String assetType) {
     this.assetType = assetType;
    }

    public String getModel() {
     return model;
    }

    public void setModel(String model) {
     this.model = model;
    }

    public LocalDate getPurchaseDate() {
     return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
     this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
     return status;
    }

    public void setStatus(String status) {
     this.status = status;
    }

    public User getCurrentHolder() {
     return currentHolder;
    }

    public void setCurrentHolder(User currentHolder) {
     this.currentHolder = currentHolder;
    }

    public LocalDateTime getCreatedAt() {
     return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
     this.createdAt = createdAt;
    }
}

   