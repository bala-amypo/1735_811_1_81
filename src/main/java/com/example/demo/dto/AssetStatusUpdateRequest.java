package com.example.demo.dto;

public class AssetStatusUpdateRequest {

    private String status;

    public AssetStatusUpdateRequest() {
    }

    public AssetStatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
