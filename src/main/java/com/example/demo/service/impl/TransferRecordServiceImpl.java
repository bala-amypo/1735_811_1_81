package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.TransferRecord;
import com.example.demo.entity.User;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.TransferRecordRepository;
import com.example.demo.service.TransferRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransferRecordServiceImpl implements TransferRecordService {

    private final TransferRecordRepository transferRepo;
    private final AssetRepository assetRepo;

    public TransferRecordServiceImpl(TransferRecordRepository transferRepo,
                                     AssetRepository assetRepo) {
        this.transferRepo = transferRepo;
        this.assetRepo = assetRepo;
    }

    @Override
    public TransferRecord createTransfer(Long assetId, TransferRecord record) {

        if (record.getTransferDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Transfer date cannot be in the future");
        }

        if (record.getFromDepartment().equals(record.getToDepartment())) {
            throw new ValidationException("From and To department must be different");
        }

        User approver = record.getApprovedBy();
        if (!"ADMIN".equalsIgnoreCase(approver.getRole())) {
            throw new ValidationException("Only ADMIN can approve transfer");
        }

        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ValidationException("Asset not found"));

        record.setAsset(asset);
        return transferRepo.save(record);
    }

    @Override
    public List<TransferRecord> getTransfersForAsset(Long assetId) {
        return transferRepo.findByAssetId(assetId);
    }

    @Override
    public TransferRecord getTransfer(Long id) {
        return transferRepo.findById(id)
                .orElseThrow(() -> new ValidationException("Transfer not found"));
    }
}
