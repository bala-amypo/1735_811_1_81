package com.example.demo.controller;

import com.example.demo.entity.Asset;
import com.example.demo.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return service.createAsset(asset);
    }

    @GetMapping
    public List<Asset> getAllAssets() {
        return service.getAllAssets();
    }

    @GetMapping("/{id}")
    public Asset getAsset(@PathVariable Long id) {
        return service.getAsset(id);
    }

    @PutMapping("/status/{id}")
    public Asset updateStatus(@PathVariable Long id,
                              @RequestParam String status) {
        return service.updateStatus(id, status);
    }
}
