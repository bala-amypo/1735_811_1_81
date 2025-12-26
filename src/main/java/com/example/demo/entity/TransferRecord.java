package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TransferRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromDepartment;

    private String toDepartment;

    private LocalDate transferDate;

    @ManyToOne
    private Asset asset;

    @ManyToOne
    private User approvedBy;

    public TransferRecord() {}

    public Long getId() {
        return id;
    }

    public String getFromDepartment() {
        return fromDepartment;
    }

    public String getToDepartment() {
        return toDepartment;
    }

    public LocalDate getTransferDate() {
        return transferDate;
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

    public void setFromDepartment(String fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    public void setToDepartment(String toDepartment) {
        this.toDepartment = toDepartment;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
}
