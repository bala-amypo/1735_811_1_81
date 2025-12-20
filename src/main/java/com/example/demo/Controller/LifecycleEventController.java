package com.example.demo.controller;

import com.example.demo.entity.LifecycleEvent;
import com.example.demo.service.LifecycleEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class LifecycleEventController {

    private final LifecycleEventService service;

    public LifecycleEventController(LifecycleEventService service) {
        this.service = service;
    }

    @PostMapping("/{assetId}/{userId}")
    public LifecycleEvent logEvent(@PathVariable Long assetId,
                                   @PathVariable Long userId,
                                   @RequestBody LifecycleEvent event) {
        return service.logEvent(assetId, userId, event);
    }

    @GetMapping("/asset/{assetId}")
    public List<LifecycleEvent> getEventsForAsset(@PathVariable Long assetId) {
        return service.getEventsForAsset(assetId);
    }

    @GetMapping("/{id}")
    public LifecycleEvent getEvent(@PathVariable Long id) {
        return service.getEvent(id);
    }
}
