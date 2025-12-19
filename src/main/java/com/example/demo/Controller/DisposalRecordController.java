package com.example.demo.controller;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.service.DisposalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disposals")
public class DisposalRecordController {

    private final DisposalRecordService service;

    public DisposalRecordController(DisposalRecordService service) {
        this.service = service;
    }

    @PostMapping("/{assetId}")
    public DisposalRecord createDisposal(@PathVariable Long assetId,
                                         @RequestBody DisposalRecord disposal) {
        return service.createDisposal(assetId, disposal);
    }

    @GetMapping
    public List<DisposalRecord> getAllDisposals() {
        return service.getAllDisposals();
    }
    @GetMapping("/{id}")
    public DisposalRecord getDisposal(@PathVariable Long id) {
        return service.getDisposal(id);
    }
}
