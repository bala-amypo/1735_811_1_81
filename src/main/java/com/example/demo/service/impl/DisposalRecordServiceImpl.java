// File: src/main/java/com/example/demo/service/impl/DisposalRecordServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.DisposalRecord;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.DisposalRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DisposalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisposalRecordServiceImpl implements DisposalRecordService {
    
    @Autowired
    private DisposalRecordRepository disposalRecordRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public DisposalRecord createDisposal(Long assetId, DisposalRecord disposal) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        
        disposal.setAsset(asset);
        DisposalRecord saved = disposalRecordRepository.save(disposal);
        
        // Update asset status to DISPOSED
        asset.setStatus("DISPOSED");
        assetRepository.save(asset);
        
        return saved;
    }
    
    @Override
    public DisposalRecord getDisposal(Long id) {
        return disposalRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found with id: " + id));
    }
}