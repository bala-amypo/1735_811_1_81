package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.LifecycleEvent;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.LifecycleEventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LifecycleEventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LifecycleEventServiceImpl implements LifecycleEventService {

    private final LifecycleEventRepository lifecycleEventRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public LifecycleEventServiceImpl(LifecycleEventRepository lifecycleEventRepository,
                                     AssetRepository assetRepository,
                                     UserRepository userRepository) {
        this.lifecycleEventRepository = lifecycleEventRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LifecycleEvent logEvent(Long assetId, Long userId, LifecycleEvent event) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        event.setAsset(asset);
        return lifecycleEventRepository.save(event);
    }

    @Override
    public List<LifecycleEvent> getEventsForAsset(Long assetId) {
        return lifecycleEventRepository.findByAssetId(assetId);
    }
}
