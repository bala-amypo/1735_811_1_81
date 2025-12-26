package com.example.demo.controller;

import com.example.demo.entity.LifecycleEvent;
import com.example.demo.service.LifecycleEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class LifecycleEventController {

    private final LifecycleEventService lifecycleEventService;

    public LifecycleEventController(LifecycleEventService lifecycleEventService) {
        this.lifecycleEventService = lifecycleEventService;
    }

    @PostMapping("/{assetId}/{userId}")
    public ResponseEntity<LifecycleEvent> logEvent(@PathVariable Long assetId, @PathVariable Long userId, @RequestBody LifecycleEvent event) {
        return ResponseEntity.ok(lifecycleEventService.logEvent(assetId, userId, event));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<LifecycleEvent>> getEventsForAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(lifecycleEventService.getEventsForAsset(assetId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LifecycleEvent> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(lifecycleEventService.getEvent(id));
    }
}