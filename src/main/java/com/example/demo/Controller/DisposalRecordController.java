package com.example.demo.controller;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.service.DisposalRecordService;
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
    public DisposalRecord create(@PathVariable Long assetId,
                                 @RequestBody DisposalRecord record) {
        return disposalRecordService.createDisposal(assetId, record);
    }

    @GetMapping
    public List<DisposalRecord> getAll() {
        return disposalRecordService.getAllDisposals();
    }

    @GetMapping("/{id}")
    public DisposalRecord getById(@PathVariable Long id) {
        return disposalRecordService.getDisposal(id);
    }
}
