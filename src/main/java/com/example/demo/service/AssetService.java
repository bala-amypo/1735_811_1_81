package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.exception.ResourceNotFoundException;

import java.util.List;

public interface AssetService {

    Asset createAsset(Asset asset);

    Asset getAsset(Long id) throws ResourceNotFoundException;

    List<Asset> getAllAssets();

    Asset updateStatus(Long id, String status) throws ResourceNotFoundException;
}
