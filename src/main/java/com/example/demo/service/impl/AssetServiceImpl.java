package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Override
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }
    
    @Override
    public Asset getAsset(Long id) {
        return assetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }
    
    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    
    @Override
    public Asset updateStatus(Long id, String status) {
        Asset asset = getAsset(id);
        asset.setStatus(status);
        return assetRepository.save(asset);
    }
}