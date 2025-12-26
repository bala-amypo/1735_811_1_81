package com.example.demo.controller;

import com.example.demo.entity.LifecycleEvent;
import com.example.demo.service.LifecycleEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class LifecycleEventController {

    @Autowired
    private LifecycleEventService lifecycleEventService;

    @PostMapping
    public LifecycleEvent logEvent(@RequestBody LifecycleEvent event) {
        Long assetId = event.getAsset() != null ? event.getAsset().getId() : null;
        Long userId = event.getPerformedBy() != null ? event.getPerformedBy().getId() : null;
        return lifecycleEventService.logEvent(assetId, userId, event);
    }

    @GetMapping("/asset/{assetId}")
    public List<LifecycleEvent> getEvents(@PathVariable Long assetId) {
        return lifecycleEventService.getEventsForAsset(assetId);
    }
}