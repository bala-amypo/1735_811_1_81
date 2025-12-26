package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String assetTag;

    @NotBlank
    private String assetType;

    @NotBlank
    private String model;

    private LocalDate purchaseDate;

    @NotBlank
    private String status;

    @ManyToOne
    private User currentHolder;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "AVAILABLE";
        }
    }

    public Asset() {}

    public Long getId() {
        return id;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getModel() {
        return model;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public User getCurrentHolder() {
        return currentHolder;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentHolder(User currentHolder) {
        this.currentHolder = currentHolder;
    }
}
