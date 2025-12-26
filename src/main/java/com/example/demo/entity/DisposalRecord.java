package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class DisposalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    private LocalDate disposalDate;

    @ManyToOne
    private Asset asset;

    @ManyToOne
    private User approvedBy;

    public DisposalRecord() {}

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public Asset getAsset() {
        return asset;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDisposalDate(LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
}
