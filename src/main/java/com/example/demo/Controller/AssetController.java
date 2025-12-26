package com.example.demo.controller;

import com.example.demo.entity.Asset;
import com.example.demo.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public Asset create(@RequestBody Asset asset) {
        return assetService.createAsset(asset);
    }

    @GetMapping
    public List<Asset> getAll() {
        return assetService.getAllAssets();
    }

    @GetMapping("/{id}")
    public Asset getById(@PathVariable Long id) {
        return assetService.getAsset(id);
    }

    @PutMapping("/status/{id}")
    public Asset updateStatus(@PathVariable Long id,
                              @RequestParam String status) {
        return assetService.updateStatus(id, status);
    }
}
