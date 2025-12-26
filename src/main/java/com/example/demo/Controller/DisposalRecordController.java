package com.example.demo.controller;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.service.DisposalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disposals")
public class DisposalRecordController {

    @Autowired
    private DisposalRecordService disposalRecordService;

    @PostMapping("/{assetId}")
    public DisposalRecord createDisposal(@PathVariable Long assetId, @RequestBody DisposalRecord dr) {
        return disposalRecordService.createDisposal(assetId, dr);
    }

    @GetMapping
    public List<DisposalRecord> getAllDisposals() {
        return disposalRecordService.getAllDisposals();
    }

    @GetMapping("/{id}")
    public DisposalRecord getDisposalById(@PathVariable Long id) {
        return disposalRecordService.getDisposal(id);
    }
}
