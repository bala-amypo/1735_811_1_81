package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.LifecycleEvent;
import com.example.demo.entity.User;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.LifecycleEventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LifecycleEventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LifecycleEventServiceImpl implements LifecycleEventService {

    private final LifecycleEventRepository eventRepo;
    private final AssetRepository assetRepo;
    private final UserRepository userRepo;

    public LifecycleEventServiceImpl(LifecycleEventRepository eventRepo,
                                     AssetRepository assetRepo,
                                     UserRepository userRepo) {
        this.eventRepo = eventRepo;
        this.assetRepo = assetRepo;
        this.userRepo = userRepo;
    }

    @Override
    public LifecycleEvent logEvent(Long assetId, Long userId, LifecycleEvent event) {

        if (event.getEventType() == null || event.getEventType().isEmpty()) {
            throw new ValidationException("Event type is required");
        }

        if (event.getEventDescription() == null || event.getEventDescription().isEmpty()) {
            throw new ValidationException("Event description cannot be empty");
        }

        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ValidationException("Asset not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        event.setAsset(asset);
        event.setPerformedBy(user);

        return eventRepo.save(event);
    }

    @Override
    public List<LifecycleEvent> getEventsForAsset(Long assetId) {
        return eventRepo.findByAssetId(assetId);
    }

    @Override
    public LifecycleEvent getEvent(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Event not found"));
    }
}
