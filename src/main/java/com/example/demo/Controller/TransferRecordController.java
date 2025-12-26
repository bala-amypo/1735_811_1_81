package com.example.demo.controller;

import com.example.demo.entity.TransferRecord;
import com.example.demo.service.TransferRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferRecordController {

    private final TransferRecordService transferRecordService;

    public TransferRecordController(TransferRecordService transferRecordService) {
        this.transferRecordService = transferRecordService;
    }

    @PostMapping("/{assetId}")
    public ResponseEntity<TransferRecord> createTransfer(@PathVariable Long assetId, @RequestBody TransferRecord record) {
        return ResponseEntity.ok(transferRecordService.createTransfer(assetId, record));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<TransferRecord>> getTransfersForAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(transferRecordService.getTransfersForAsset(assetId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferRecord> getTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(transferRecordService.getTransfer(id));
    }
}