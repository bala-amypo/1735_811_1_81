package com.example.demo.controller;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.service.DisposalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disposals")
public class DisposalRecordController {

    private final DisposalRecordService disposalRecordService;

    public DisposalRecordController(DisposalRecordService disposalRecordService) {
        this.disposalRecordService = disposalRecordService;
    }

    @PostMapping("/{assetId}")
    public ResponseEntity<DisposalRecord> createDisposal(@PathVariable Long assetId, @RequestBody DisposalRecord disposal) {
        return ResponseEntity.ok(disposalRecordService.createDisposal(assetId, disposal));
    }

    @GetMapping
    public ResponseEntity<List<DisposalRecord>> getAllDisposals() {
        return ResponseEntity.ok(disposalRecordService.getAllDisposals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisposalRecord> getDisposal(@PathVariable Long id) {
        return ResponseEntity.ok(disposalRecordService.getDisposal(id));
    }
}