package com.example.demo.service;

import com.example.demo.entity.LifecycleEvent;
import java.util.List;

public interface LifecycleEventService {

    LifecycleEvent logEvent(Long assetId, Long userId, LifecycleEvent event);

    List<LifecycleEvent> getEventsForAsset(Long assetId);

    LifecycleEvent getEvent(Long id);
}
