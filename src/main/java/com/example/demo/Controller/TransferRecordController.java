package com.example.demo.controller;

import com.example.demo.entity.TransferRecord;
import com.example.demo.service.TransferRecordService;
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
    public TransferRecord createTransfer(@PathVariable Long assetId, @RequestBody TransferRecord tr) {
        return transferRecordService.createTransfer(assetId, tr);
    }

    @GetMapping("/asset/{assetId}")
    public List<TransferRecord> getTransfersForAsset(@PathVariable Long assetId) {
        return transferRecordService.getTransfersForAsset(assetId);
    }

    @GetMapping("/{id}")
    public TransferRecord getTransferById(@PathVariable Long id) {
        return transferRecordService.getTransfer(id);
    }
}
