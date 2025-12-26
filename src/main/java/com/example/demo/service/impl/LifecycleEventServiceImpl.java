// File: src/main/java/com/example/demo/service/impl/LifecycleEventServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.LifecycleEvent;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.LifecycleEventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LifecycleEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LifecycleEventServiceImpl implements LifecycleEventService {
    
    @Autowired
    private LifecycleEventRepository lifecycleEventRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public LifecycleEvent logEvent(Long assetId, Long userId, LifecycleEvent event) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        event.setAsset(asset);
        event.setPerformedBy(user);
        
        return lifecycleEventRepository.save(event);
    }
    
    @Override
    public List<LifecycleEvent> getEventsForAsset(Long assetId) {
        return lifecycleEventRepository.findByAsset_Id(assetId);
    }
}