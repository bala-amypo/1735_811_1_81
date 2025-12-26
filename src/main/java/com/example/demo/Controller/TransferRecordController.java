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
    public TransferRecord create(@PathVariable Long assetId,
                                 @RequestBody TransferRecord record) {
        return transferRecordService.createTransfer(assetId, record);
    }

    @GetMapping("/asset/{assetId}")
    public List<TransferRecord> getByAsset(@PathVariable Long assetId) {
        return transferRecordService.getTransfersForAsset(assetId);
    }

    @GetMapping("/{id}")
    public TransferRecord getById(@PathVariable Long id) {
        return transferRecordService.getTransfer(id);
    }
}
